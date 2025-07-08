#!/bin/bash

echo "======================================================"
echo "    TheKnife Restaurant Management System"
echo "======================================================"
echo

echo "Starting TheKnife application..."
java -Djava.util.logging.config.file=/dev/null -jar bin/TheKnife-portable.jar

if [ $? -ne 0 ]; then
    echo
    echo "ERROR: Failed to start the application."
    echo "Make sure Java is installed."
    read -p "Press Enter to continue..."
fi
