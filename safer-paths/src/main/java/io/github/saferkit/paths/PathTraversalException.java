package io.github.saferkit.paths;

/**
 * Thrown when a path traversal attempt is detected.
 * <p>
 * This exception indicates an attempt to escape a base directory through
 * path manipulation such as using ".." components, absolute paths, or symlinks
 * pointing outside the allowed directory tree.
 * </p>
 * <p>
 * This is a security-critical exception that should be logged and monitored.
 * </p>
 *
 * @since 1.0.0
 */
public class PathTraversalException extends IllegalArgumentException {

    private final String attemptedPath;
    private final String baseDirectory;

    /**
     * Constructs a new PathTraversalException with detailed context.
     *
     * @param message the detail message
     * @param attemptedPath the path that attempted to escape the base directory
     * @param baseDirectory the base directory that should contain all resolved paths
     */
    public PathTraversalException(String message, String attemptedPath, String baseDirectory) {
        super(message);
        this.attemptedPath = attemptedPath;
        this.baseDirectory = baseDirectory;
    }

    /**
     * Returns the path that attempted to escape the base directory.
     *
     * @return the attempted path
     */
    public String getAttemptedPath() {
        return attemptedPath;
    }

    /**
     * Returns the base directory that should contain all resolved paths.
     *
     * @return the base directory
     */
    public String getBaseDirectory() {
        return baseDirectory;
    }
}
