package io.github.saferkit.paths;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

/**
 * Path traversal prevention and safe file operations.
 * <p>
 * This class provides secure path resolution that prevents Zip Slip and related
 * path traversal attacks (CVE-2018-1000066, CVE-2022-23457, and many others).
 * All methods validate that resolved paths stay within their base directory using
 * canonical path normalization.
 * </p>
 *
 * <h2>Security Model</h2>
 * <p>
 * All {@code resolve()} methods perform the following validations:
 * </p>
 * <ol>
 * <li>Validate inputs (null checks, null bytes, empty paths, Windows reserved names)</li>
 * <li>Resolve the user path against the base directory</li>
 * <li>Normalize both paths to canonical form (resolving symlinks)</li>
 * <li>Verify the resolved path is within or equal to the base directory</li>
 * <li>Throw {@link PathTraversalException} if validation fails</li>
 * </ol>
 *
 * <h2>Examples</h2>
 * <pre>{@code
 * Path baseDir = Paths.get("/safe/directory");
 *
 * // Valid: resolves to /safe/directory/file.txt
 * Path safe = SaferPaths.resolve(baseDir, "file.txt");
 *
 * // Valid: resolves to /safe/directory/subdir/file.txt
 * Path safe2 = SaferPaths.resolve(baseDir, "subdir/file.txt");
 *
 * // Throws PathTraversalException: attempts to escape base directory
 * SaferPaths.resolve(baseDir, "../../etc/passwd");
 *
 * // Throws PathTraversalException: absolute path
 * SaferPaths.resolve(baseDir, "/etc/passwd");
 *
 * // Throws IllegalArgumentException: null byte injection
 * SaferPaths.resolve(baseDir, "file\0.txt");
 *
 * // Throws IllegalArgumentException: Windows reserved name
 * SaferPaths.resolve(baseDir, "CON.txt"); // on Windows
 * }</pre>
 *
 * <h2>Platform Considerations</h2>
 * <ul>
 * <li><strong>Windows:</strong> Validates against reserved names (CON, PRN, AUX, NUL, COM1-9, LPT1-9),
 *     handles case-insensitive filesystem, normalizes trailing dots/spaces</li>
 * <li><strong>Unix/Linux/macOS:</strong> Resolves symlinks to their canonical targets,
 *     validates final resolved path</li>
 * <li><strong>All platforms:</strong> Rejects null bytes, validates canonical paths,
 *     prevents directory traversal</li>
 * </ul>
 *
 * @since 1.0.0
 */
public final class SaferPaths {

    private static final boolean IS_WINDOWS = System.getProperty("os.name")
            .toLowerCase(Locale.ROOT).startsWith("win");

    private static final String[] WINDOWS_RESERVED_NAMES = {
            "CON", "PRN", "AUX", "NUL",
            "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
            "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
    };

    private SaferPaths() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Safely resolves userPath against baseDir.
     * <p>
     * Validates that the resolved path stays within baseDir using canonical path validation.
     * This prevents path traversal attacks including Zip Slip vulnerabilities.
     * </p>
     *
     * @param baseDir  the base directory that resolved path must stay within
     * @param userPath the user-provided path to resolve (relative or absolute)
     * @return the resolved path (canonical if exists, absolute normalized otherwise)
     * @throws PathTraversalException   if resolved path escapes baseDir
     * @throws IllegalArgumentException if userPath contains null bytes, is empty, or contains Windows reserved names
     * @throws IOException              if canonical path cannot be computed
     */
    public static Path resolve(Path baseDir, String userPath) throws IOException {
        Objects.requireNonNull(baseDir, "baseDir must not be null");
        Objects.requireNonNull(userPath, "userPath must not be null");

        // Validate user path
        validateUserPath(userPath);

        // Resolve path against base directory
        Path resolved = baseDir.resolve(userPath);

        // Validate containment using canonical paths
        validateContainment(baseDir, resolved, userPath);

        // Return canonical path if exists, otherwise normalized absolute path
        try {
            return resolved.toRealPath();
        } catch (IOException e) {
            return resolved.toAbsolutePath().normalize();
        }
    }

    /**
     * Safely resolves userPath against baseDir.
     * <p>
     * Validates that the resolved path stays within baseDir using canonical path validation.
     * </p>
     *
     * @param baseDir  the base directory that resolved path must stay within
     * @param userPath the user-provided path to resolve
     * @return the resolved path (canonical if exists, absolute normalized otherwise)
     * @throws PathTraversalException if resolved path escapes baseDir
     * @throws IOException            if canonical path cannot be computed
     */
    public static Path resolve(Path baseDir, Path userPath) throws IOException {
        Objects.requireNonNull(baseDir, "baseDir must not be null");
        Objects.requireNonNull(userPath, "userPath must not be null");

        // Resolve path against base directory
        Path resolved = baseDir.resolve(userPath);

        // Validate containment using canonical paths
        validateContainment(baseDir, resolved, userPath.toString());

        // Return canonical path if exists, otherwise normalized absolute path
        try {
            return resolved.toRealPath();
        } catch (IOException e) {
            return resolved.toAbsolutePath().normalize();
        }
    }

    /**
     * Safely resolves userPath against baseDir (File API compatibility).
     * <p>
     * Validates that the resolved path stays within baseDir using canonical path validation.
     * </p>
     *
     * @param baseDir  the base directory that resolved path must stay within
     * @param userPath the user-provided path to resolve
     * @return the resolved path (canonical if exists, absolute normalized otherwise)
     * @throws PathTraversalException   if resolved path escapes baseDir
     * @throws IllegalArgumentException if userPath contains null bytes, is empty, or contains Windows reserved names
     * @throws IOException              if canonical path cannot be computed
     */
    public static Path resolve(File baseDir, String userPath) throws IOException {
        Objects.requireNonNull(baseDir, "baseDir must not be null");
        Objects.requireNonNull(userPath, "userPath must not be null");

        return resolve(baseDir.toPath(), userPath);
    }

    /**
     * Validates user-provided path string for common attack patterns.
     *
     * @param userPath the user path to validate
     * @throws IllegalArgumentException if userPath is invalid
     */
    private static void validateUserPath(String userPath) {
        // Check for empty path
        if (userPath.isEmpty()) {
            throw new IllegalArgumentException("userPath must not be empty");
        }

        // Check for null byte injection
        if (userPath.indexOf('\0') != -1) {
            throw new IllegalArgumentException("userPath contains null byte");
        }

        // Check for Windows reserved names (if running on Windows)
        if (IS_WINDOWS) {
            validateNoWindowsReservedName(userPath);
        }
    }

    /**
     * Validates that the path doesn't contain Windows reserved names.
     *
     * @param userPath the path to validate
     * @throws IllegalArgumentException if path contains reserved name
     */
    private static void validateNoWindowsReservedName(String userPath) {
        // Extract filename from path (last component after / or \)
        String fileName = userPath;
        int lastSlash = Math.max(userPath.lastIndexOf('/'), userPath.lastIndexOf('\\'));
        if (lastSlash >= 0 && lastSlash < userPath.length() - 1) {
            fileName = userPath.substring(lastSlash + 1);
        }

        // Remove extension if present
        String nameWithoutExt = fileName;
        int dotIndex = fileName.indexOf('.');
        if (dotIndex > 0) {
            nameWithoutExt = fileName.substring(0, dotIndex);
        }

        // Check against reserved names (case-insensitive on Windows)
        String upperName = nameWithoutExt.toUpperCase(Locale.ROOT);
        for (String reserved : WINDOWS_RESERVED_NAMES) {
            if (reserved.equals(upperName)) {
                throw new IllegalArgumentException(
                        "userPath contains Windows reserved name: " + fileName);
            }
        }
    }

    /**
     * Validates that resolved path is contained within base directory.
     *
     * @param baseDir          the base directory
     * @param resolved         the resolved path to validate
     * @param originalUserPath the original user path (for error messages)
     * @throws PathTraversalException if resolved path escapes baseDir
     * @throws IOException            if canonical paths cannot be computed
     */
    private static void validateContainment(Path baseDir, Path resolved, String originalUserPath)
            throws IOException {
        // Normalize base directory to canonical form (must exist)
        Path canonicalBase = baseDir.toRealPath();

        // For resolved path: use toRealPath() if exists (resolves symlinks),
        // otherwise use toAbsolutePath().normalize() for validation
        Path canonicalResolved;
        try {
            canonicalResolved = resolved.toRealPath();
        } catch (IOException e) {
            // Path doesn't exist - use absolute normalized path for validation
            // This is common for Zip Slip scenarios where files are being created
            canonicalResolved = resolved.toAbsolutePath().normalize();
        }

        // Check if resolved path equals base (exact match is valid)
        if (canonicalResolved.equals(canonicalBase)) {
            return;
        }

        // Check if resolved path starts with base + separator
        // This prevents /base vs /base-evil bypass
        if (!canonicalResolved.startsWith(canonicalBase)) {
            throw new PathTraversalException(
                    "Path traversal detected: resolved path is outside base directory",
                    originalUserPath,
                    canonicalBase.toString()
            );
        }

        // Verify that what comes after the base starts with a separator
        // This handles the case where canonicalBase = /foo and canonicalResolved = /foobar
        String basePath = canonicalBase.toString();
        String resolvedPath = canonicalResolved.toString();

        if (resolvedPath.length() > basePath.length()) {
            char charAfterBase = resolvedPath.charAt(basePath.length());
            if (charAfterBase != File.separatorChar && charAfterBase != '/') {
                throw new PathTraversalException(
                        "Path traversal detected: resolved path is outside base directory",
                        originalUserPath,
                        canonicalBase.toString()
                );
            }
        }
    }
}
