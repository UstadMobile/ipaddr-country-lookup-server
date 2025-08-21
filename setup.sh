#!/bin/bash

# GeoLite2 Database Setup Script
# This script helps developers set up the GeoLite2 database for development

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RESOURCES_DIR="$SCRIPT_DIR/src/main/resources"
DATABASE_FILE="GeoLite2-Country.mmdb"
TARGET_PATH="$RESOURCES_DIR/$DATABASE_FILE"

echo "🌍 GeoIP Service Database Setup"
echo "================================"

if [[ -f "$TARGET_PATH" ]]; then
    echo "✅ Database file already exists at: $TARGET_PATH"
    echo "   File size: $(du -h "$TARGET_PATH" | cut -f1)"
    echo "   Last modified: $(stat -c %y "$TARGET_PATH" 2>/dev/null || stat -f %Sm "$TARGET_PATH")"
    echo ""
    read -p "Do you want to replace it? (y/N): " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Setup cancelled. Using existing database."
        exit 0
    fi
fi

# Create resources directory if it doesn't exist
mkdir -p "$RESOURCES_DIR"

echo "📋 To set up the GeoLite2 database, you need to:"
echo ""
echo "1. 🔗 Visit: https://www.maxmind.com/en/geolite2/signup"
echo "2. 📝 Create a free MaxMind account"
echo "3. 📥 Download GeoLite2-Country database (Binary/MMDB format)"
echo "4. 📁 The downloaded file should be named: $DATABASE_FILE"
echo ""
echo "💡 Common download locations:"
echo "   - ~/Downloads/$DATABASE_FILE"
echo "   - ~/Desktop/$DATABASE_FILE"
echo ""

# Try to find the file in common locations
COMMON_LOCATIONS=(
    "$HOME/Downloads/$DATABASE_FILE"
    "$HOME/Desktop/$DATABASE_FILE"
    "$HOME/Documents/$DATABASE_FILE"
    "./$DATABASE_FILE"
)

FOUND_FILE=""
for location in "${COMMON_LOCATIONS[@]}"; do
    if [[ -f "$location" ]]; then
        FOUND_FILE="$location"
        echo "🎯 Found database file at: $location"
        break
    fi
done

if [[ -n "$FOUND_FILE" ]]; then
    echo ""
    read -p "Use this file? (Y/n): " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[Nn]$ ]]; then
        FOUND_FILE=""
    fi
fi

# If not found or user declined, ask for path
if [[ -z "$FOUND_FILE" ]]; then
    echo ""
    echo "🔍 Please provide the full path to your downloaded $DATABASE_FILE file:"
    read -p "Path: " DATABASE_PATH

    if [[ ! -f "$DATABASE_PATH" ]]; then
        echo "❌ Error: File not found at: $DATABASE_PATH"
        echo "Please check the path and try again."
        exit 1
    fi
    FOUND_FILE="$DATABASE_PATH"
fi

# Copy the file
echo ""
echo "📋 Copying database file..."
cp "$FOUND_FILE" "$TARGET_PATH"

# Verify the copy
if [[ -f "$TARGET_PATH" ]]; then
    FILE_SIZE=$(du -h "$TARGET_PATH" | cut -f1)
    echo "✅ Database setup complete!"
    echo "   📍 Location: $TARGET_PATH"
    echo "   📏 Size: $FILE_SIZE"
    echo ""
    echo "🚀 You can now run the application with:"
    echo "   ./gradlew run"
    echo ""
    echo "🧪 Test the API with:"
    echo "   curl http://localhost:8080/country"
else
    echo "❌ Error: Failed to copy database file"
    exit 1
fi

# Add to gitignore if needed
GITIGNORE_PATH="$SCRIPT_DIR/.gitignore"
if [[ -f "$GITIGNORE_PATH" ]]; then
    if ! grep -q "*.mmdb" "$GITIGNORE_PATH"; then
        echo "" >> "$GITIGNORE_PATH"
        echo "# GeoIP Database files - DO NOT COMMIT" >> "$GITIGNORE_PATH"
        echo "*.mmdb" >> "$GITIGNORE_PATH"
        echo "GeoLite2-*" >> "$GITIGNORE_PATH"
        echo "📝 Updated .gitignore to exclude database files"
    fi
fi

echo "Setup complete! Happy coding! "