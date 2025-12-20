package io.github.saferkit.paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class SaferPathsTest {

    @TempDir
    private Path tempDir;

    // ========== Happy Path ==========

    @Test
    void testResolve_ValidRelativePath() throws IOException {
        Path file = Files.createFile(tempDir.resolve("file.txt"));

        Path resolved = SaferPaths.resolve(tempDir, "file.txt");

        assertEquals(file.toRealPath(), resolved);
    }

    @Test
    void testResolve_ValidSubdirectory() throws IOException {
        Path subdir = Files.createDirectory(tempDir.resolve("subdir"));
        Path file = Files.createFile(subdir.resolve("file.txt"));

        Path resolved = SaferPaths.resolve(tempDir, "subdir/file.txt");

        assertEquals(file.toRealPath(), resolved);
    }

    @Test
    void testResolve_ExactBaseDirectory() throws IOException {
        Path resolved = SaferPaths.resolve(tempDir, ".");

        assertEquals(tempDir.toRealPath(), resolved);
    }

    @Test
    void testResolve_WithPathArgument() throws IOException {
        Path file = Files.createFile(tempDir.resolve("file.txt"));

        Path resolved = SaferPaths.resolve(tempDir, Paths.get("file.txt"));

        assertEquals(file.toRealPath(), resolved);
    }

    @Test
    void testResolve_WithFileArgument() throws IOException {
        Path file = Files.createFile(tempDir.resolve("file.txt"));

        Path resolved = SaferPaths.resolve(tempDir.toFile(), "file.txt");

        assertEquals(file.toRealPath(), resolved);
    }

    // ========== Traversal Attacks ==========

    @Test
    void testResolve_ParentTraversal_Throws() throws IOException {
        PathTraversalException ex = assertThrows(PathTraversalException.class,
                () -> SaferPaths.resolve(tempDir, "../etc/passwd"));

        assertEquals("../etc/passwd", ex.getAttemptedPath());
        assertEquals(tempDir.toRealPath().toString(), ex.getBaseDirectory());
    }

    @Test
    void testResolve_MultipleParentTraversal_Throws() {
        assertThrows(PathTraversalException.class,
                () -> SaferPaths.resolve(tempDir, "../../../etc/shadow"));
    }

    @Test
    void testResolve_AbsolutePath_Throws() {
        assertThrows(PathTraversalException.class,
                () -> SaferPaths.resolve(tempDir, "/etc/passwd"));
    }

    @Test
    void testResolve_MixedSeparators_Throws() {
        assertThrows(PathTraversalException.class,
                () -> SaferPaths.resolve(tempDir, "..\\..\\windows\\system32"));
    }

    @Test
    void testResolve_DotDotInMiddle_Throws() {
        assertThrows(PathTraversalException.class,
                () -> SaferPaths.resolve(tempDir, "subdir/../../escape.txt"));
    }

    // ========== Edge Cases ==========

    @Test
    void testResolve_NullByteInjection_Throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> SaferPaths.resolve(tempDir, "file\0.txt"));

        assertTrue(ex.getMessage().contains("null byte"));
    }

    @Test
    void testResolve_EmptyPath_Throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> SaferPaths.resolve(tempDir, ""));

        assertTrue(ex.getMessage().contains("empty"));
    }

    @Test
    void testResolve_NullBaseDir_Throws() {
        assertThrows(NullPointerException.class,
                () -> SaferPaths.resolve((Path) null, "file.txt"));
    }

    @Test
    void testResolve_NullUserPath_Throws() {
        assertThrows(NullPointerException.class,
                () -> SaferPaths.resolve(tempDir, (String) null));
    }

    @Test
    void testResolve_UnicodeFileName() throws IOException {
        Path file = Files.createFile(tempDir.resolve("файл.txt"));

        Path resolved = SaferPaths.resolve(tempDir, "файл.txt");

        assertEquals(file.toRealPath(), resolved);
    }

    // ========== Platform Specific ==========

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testResolve_WindowsReservedNames_Throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> SaferPaths.resolve(tempDir, "CON.txt"));

        assertTrue(ex.getMessage().contains("reserved name"));
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testResolve_WindowsReservedName_PRN_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> SaferPaths.resolve(tempDir, "PRN"));
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testResolve_WindowsReservedName_COM1_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> SaferPaths.resolve(tempDir, "COM1.dat"));
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void testResolve_SymlinkInsideBase() throws IOException {
        Path file = Files.createFile(tempDir.resolve("target.txt"));
        Path symlink = tempDir.resolve("link.txt");
        Files.createSymbolicLink(symlink, file);

        Path resolved = SaferPaths.resolve(tempDir, "link.txt");

        assertEquals(file.toRealPath(), resolved);
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void testResolve_SymlinkOutsideBase_Throws(@TempDir Path outsideDir) throws IOException {
        Path outsideFile = Files.createFile(outsideDir.resolve("outside.txt"));

        // Create symlink inside tempDir pointing outside
        Path symlink = tempDir.resolve("evil-link.txt");
        Files.createSymbolicLink(symlink, outsideFile);

        // Should throw because symlink points outside base
        assertThrows(PathTraversalException.class,
                () -> SaferPaths.resolve(tempDir, "evil-link.txt"));
    }

    // ========== Exception Details ==========

    @Test
    void testPathTraversalException_ContainsAttemptedPath() {
        PathTraversalException ex = assertThrows(PathTraversalException.class,
                () -> SaferPaths.resolve(tempDir, "../escape"));

        assertNotNull(ex.getAttemptedPath());
        assertEquals("../escape", ex.getAttemptedPath());
    }

    @Test
    void testPathTraversalException_ContainsBaseDirectory() throws IOException {
        PathTraversalException ex = assertThrows(PathTraversalException.class,
                () -> SaferPaths.resolve(tempDir, "../escape"));

        assertNotNull(ex.getBaseDirectory());
        assertTrue(ex.getBaseDirectory().contains(tempDir.toRealPath().toString()));
    }

    // ========== Integration Tests ==========

    @ParameterizedTest
    @ValueSource(strings = {
            "../../etc/passwd",
            "../../../etc/shadow",
            "..\\..\\windows\\system32",
            "/etc/passwd",
            "subdir/../../escape.txt"
    })
    void testKnownMaliciousPaths_AllThrow(String maliciousPath) {
        assertThrows(Exception.class,
                () -> SaferPaths.resolve(tempDir, maliciousPath));
    }

    @Test
    void testResolve_NullByteInParameterized() {
        assertThrows(IllegalArgumentException.class,
                () -> SaferPaths.resolve(tempDir, "file\0.txt"));
    }
}
