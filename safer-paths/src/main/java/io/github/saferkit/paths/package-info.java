/**
 * Provides path traversal prevention and safe file operations.
 * <p>
 * This package prevents Zip Slip and related path traversal attacks through
 * canonical path validation, symlink handling, and secure path resolution.
 * All operations fail closed - throwing exceptions on security violations.
 * </p>
 *
 * @see io.github.saferkit.paths.SaferPaths
 */
@NullMarked
package io.github.saferkit.paths;

import org.jspecify.annotations.NullMarked;
