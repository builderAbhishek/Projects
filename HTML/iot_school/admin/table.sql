-- Create `users` table first
CREATE TABLE IF NOT EXISTS users (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'author', 'user') DEFAULT 'author',
    date_joined TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Create `categories` table
CREATE TABLE IF NOT EXISTS categories (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Create `courses` table with foreign key references to `categories` and `users` (author)
CREATE TABLE IF NOT EXISTS courses (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    category_id INT(11) NOT NULL,  -- Foreign key referencing `categories(id)`
    author_id INT(11) NOT NULL,    -- Foreign key referencing `users(id)`
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Create `tutorials` table with foreign key references to `courses`
CREATE TABLE IF NOT EXISTS tutorials (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    tutorial_number INT(11) NOT NULL,
    link_name VARCHAR(255) NOT NULL UNIQUE,  -- SEO-friendly URL
    content TEXT NOT NULL,
    author_id INT(11) NOT NULL,  -- Foreign key referencing `users(id)`
    meta_description TEXT,
    meta_tags TEXT,
    meta_image VARCHAR(255),  -- Image URL for social sharing
    course_id INT(11) NOT NULL,  -- Foreign key referencing `courses(id)`
    tags VARCHAR(255),  -- Comma-separated tags for the tutorial
    article_image VARCHAR(255),  -- Image related to the tutorial
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Create `comments` table to allow users to comment on tutorials
CREATE TABLE IF NOT EXISTS comments (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    tutorial_id INT(11) NOT NULL,  -- Foreign key referencing `tutorials(id)`
    user_id INT(11) NOT NULL,      -- Foreign key referencing `users(id)`
    content TEXT NOT NULL,
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tutorial_id) REFERENCES tutorials(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Create `tags` table (optional) to manage tags separately
CREATE TABLE IF NOT EXISTS tags (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Create a linking table `tutorial_tags` to associate tutorials with tags
CREATE TABLE IF NOT EXISTS tutorial_tags (
    tutorial_id INT(11) NOT NULL,  -- Foreign key referencing `tutorials(id)`
    tag_id INT(11) NOT NULL,       -- Foreign key referencing `tags(id)`
    PRIMARY KEY (tutorial_id, tag_id),
    FOREIGN KEY (tutorial_id) REFERENCES tutorials(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Create `course_progress` table for tracking users' progress in courses
CREATE TABLE IF NOT EXISTS course_progress (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    user_id INT(11) NOT NULL,    -- Foreign key referencing `users(id)`
    course_id INT(11) NOT NULL,  -- Foreign key referencing `courses(id)`
    progress INT(11) DEFAULT 0,  -- Percentage of progress (0 to 100)
    date_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
) ENGINE=InnoDB;





-- CREATE DATABASE
CREATE DATABASE IF NOT EXISTS iot_school;
USE iot_school;

-- Table for storing users (authors, admins, etc.)
CREATE TABLE users (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'author', 'subscriber') NOT NULL DEFAULT 'author',
    status ENUM('active', 'inactive') NOT NULL DEFAULT 'active',
    date_registered TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for storing courses (tutorials can be part of courses)
CREATE TABLE courses (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    category_id INT(11) NOT NULL,
    author_id INT(11),
    price VARCHAR(10) DEFAULT NULL,
    modules VARCHAR(255) DEFAULT NULL,
    difficulty VARCHAR(50) DEFAULT NULL,
    type VARCHAR(100) DEFAULT NULL;
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Table for storing categories for organizing courses
CREATE TABLE categories (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT
);

-- Table for storing tutorials (main content table)
CREATE TABLE tutorials (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    tutorial_number INT(11) NOT NULL,
    link_name VARCHAR(255) NOT NULL UNIQUE,  -- SEO-friendly URL
    content TEXT NOT NULL,
    author_id INT(11),  -- Foreign key for author
    meta_description TEXT,  -- SEO meta description
    meta_keywords VARCHAR(255),  -- SEO meta tags
    meta_image VARCHAR(255),  -- Meta image for social media sharing
    course_id INT(11) DEFAULT NULL,  -- Foreign key for courses
    tags VARCHAR(255),  -- Comma-separated tags for SEO
    article_image VARCHAR(255),  -- Main article image (thumbnail)
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- Table for storing tutorial views (tracking user views for each tutorial)
CREATE TABLE tutorial_views (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    tutorial_id INT(11),
    user_id INT(11),
    date_viewed TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tutorial_id) REFERENCES tutorials(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Table for storing user comments on tutorials
CREATE TABLE comments (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    tutorial_id INT(11),
    user_id INT(11),
    comment TEXT NOT NULL,
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tutorial_id) REFERENCES tutorials(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Table for storing media (images, files, etc.)
CREATE TABLE media (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_type ENUM('image', 'audio', 'video', 'document') NOT NULL,
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for storing tags (for tutorials and courses)
CREATE TABLE tags (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Table for associating tags with tutorials
CREATE TABLE tutorial_tags (
    tutorial_id INT(11),
    tag_id INT(11),
    PRIMARY KEY (tutorial_id, tag_id),
    FOREIGN KEY (tutorial_id) REFERENCES tutorials(id),
    FOREIGN KEY (tag_id) REFERENCES tags(id)
);

-- Table for associating tags with courses
CREATE TABLE course_tags (
    course_id INT(11),
    tag_id INT(11),
    PRIMARY KEY (course_id, tag_id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (tag_id) REFERENCES tags(id)
);

-- Table for storing subscription plans (if applicable for premium content)
CREATE TABLE subscription_plans (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    duration INT(11) NOT NULL,  -- Duration in days
    description TEXT,
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for storing user subscriptions (who is subscribed to which plan)
CREATE TABLE user_subscriptions (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    user_id INT(11),
    plan_id INT(11),
    start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_date TIMESTAMP,
    status ENUM('active', 'inactive', 'expired') DEFAULT 'active',
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (plan_id) REFERENCES subscription_plans(id)
);

-- Table for storing contact form messages
CREATE TABLE contact_messages (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for storing website settings (like meta title, logo, etc.)
CREATE TABLE website_settings (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(255) NOT NULL UNIQUE,
    setting_value TEXT NOT NULL
);
