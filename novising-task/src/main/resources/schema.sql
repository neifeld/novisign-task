/*
 * Database Schema for Slideshow Management System
 *
 * This script creates the necessary tables and indexes for managing slideshows,
 * images, and proof of play records. It includes tables for storing image details,
 * slideshow information, associations between slideshows and images, and proof of
 * play data.
 *
 * Tables:
 * - images: Stores information about individual images
 * - slideshows: Contains details about slideshows
 * - slideshow_images: Join table linking slideshows and images
 * - slideshow_proof_of_play: Records when images in slideshows are played
 *
 * Indexes are created on frequently queried columns for improved performance.
 */

-- Drop tables if they exist (with IF EXISTS to prevent errors)
DROP TABLE IF EXISTS slideshow_proof_of_play;
DROP TABLE IF EXISTS slideshow_images;
DROP TABLE IF EXISTS slideshows;
DROP TABLE IF EXISTS images;

/*
 * Create images table
 *
 * Stores information about individual images used in slideshows.
 *
 * Columns:
 * - id: Unique identifier for each image (auto-incrementing)
 * - name: Name of the image (required)
 * - url: URL or path to the image file (required)
 * - description: Optional description of the image
 * - duration: Display duration of the image in seconds (default: 10)
 */
CREATE TABLE IF NOT EXISTS images
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    url         VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    duration    INT          NOT NULL DEFAULT 10
);

/*
 * Create slideshows table
 *
 * Stores information about slideshows.
 *
 * Columns:
 * - id: Unique identifier for each slideshow (auto-incrementing)
 * - name: Name of the slideshow (required)
 * - description: Optional description of the slideshow
 * - image_ids: Comma-separated list of image IDs (deprecated, use slideshow_images table instead)
 * - created_at: Timestamp of when the slideshow was created
 */
CREATE TABLE IF NOT EXISTS slideshows
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    image_ids   TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

/*
 * Create join table for slideshows and images
 *
 * Associates images with slideshows and specifies their order.
 *
 * Columns:
 * - slideshow_id: ID of the slideshow (foreign key to slideshows table)
 * - image_id: ID of the image (foreign key to images table)
 * - position: Order of the image within the slideshow
 */
CREATE TABLE IF NOT EXISTS slideshow_images
(
    slideshow_id INTEGER REFERENCES slideshows (id) ON DELETE CASCADE,
    image_id     INTEGER REFERENCES images (id) ON DELETE CASCADE,
    position     INTEGER NOT NULL,
    PRIMARY KEY (slideshow_id, image_id)
);

/*
 * Create proof of play table
 *
 * Records when images in slideshows are played.
 *
 * Columns:
 * - id: Unique identifier for each play record (auto-incrementing)
 * - slideshow_id: ID of the slideshow being played
 * - image_id: ID of the image being played
 * - played_at: Timestamp of when the image was played
 */
CREATE TABLE IF NOT EXISTS slideshow_proof_of_play
(
    id           SERIAL PRIMARY KEY,
    slideshow_id BIGINT NOT NULL,
    image_id     BIGINT NOT NULL,
    played_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (slideshow_id) REFERENCES slideshows (id) ON DELETE CASCADE
);

/*
 * Create indexes for better performance
 *
 * These indexes improve query performance on frequently accessed columns.
 */
CREATE INDEX IF NOT EXISTS idx_images_url ON images (url);
CREATE INDEX IF NOT EXISTS idx_slideshows_name ON slideshows (name);
CREATE INDEX IF NOT EXISTS idx_proof_of_play_slideshow_id ON slideshow_proof_of_play (slideshow_id);
CREATE INDEX IF NOT EXISTS idx_proof_of_play_image_id ON slideshow_proof_of_play (image_id);