import sqlite3
import re
from bs4 import BeautifulSoup
import shutil

def log_print(*args, **kwargs):
    with open("hadith_processing_log.txt", 'w', encoding='utf-8') as log_file:
        print(*args, **kwargs, file=log_file)

def clean_html_text(html_content):
    if not html_content:
        return ""
    soup = BeautifulSoup(html_content, 'html.parser')
    
    # Define markers. We use text markers because get_text() will strip HTML tags.
    br_marker = "||BR||"
# Define markers for each style
    style_markers = {
        'red': ("{{RED}}", "{{/RED}}"),
        'darkred': ("{{DARKRED}}", "{{/DARKRED}}"),
        'blue': ("{{BLUE}}", "{{/BLUE}}"),
        'green': ("{{GREEN}}", "{{/GREEN}}"),
    }

    # Replace <br> tags with a marker
    for br in soup.find_all("br"):
        br.replace_with(br_marker)
    

    style_regex_p1 = re.compile(r'color:rgb\(0, 0, 255\)')
    start, end = style_markers['blue']
    for span in soup.find_all('span', style=style_regex_p1):
        span.insert_before(start)
        span.insert_after(end)
        span.unwrap()

    style_regex_p2 = re.compile(r'color:rgb\(0, 128, 0\)')
    start, end = style_markers['green']
    for span in soup.find_all('span', style=style_regex_p2):
        span.insert_before(start)
        span.insert_after(end)
        span.unwrap()

    style_regex_p3 = re.compile(r'color:rgb\(255, 0, 0\)')
    start, end = style_markers['red']
    for span in soup.find_all('span', style=style_regex_p3):
        span.insert_before(start)
        span.insert_after(end)
        span.unwrap()

    style_regex_p4 = re.compile(r'color:rgb\(152, 0, 0\)')
    start, end = style_markers['darkred']
    for span in soup.find_all('span', style=style_regex_p4):
        span.insert_before(start)
        span.insert_after(end)
        span.unwrap()

    text_parts = []
    # Find all major block-level elements that should be separated by newlines
    for element in soup.find_all(['p', 'li']):
        # If this element is nested inside another element we're already processing, skip it.
        if element.find_parent(['p', 'li']):
            continue
            
        # Get text from the element, replacing any internal tags with a space.
        # Our marker will be preserved.
        text = element.get_text(separator=' ', strip=True)
        
        # Consolidate whitespace, but protect the marker by removing spaces around it
        text = re.sub(r'\s*' + re.escape(br_marker) + r'\s*', br_marker, text)
        # Now collapse all other whitespace
        text = re.sub(r'\s+', ' ', text).strip()

        # Finally, replace the marker with a newline
        text = text.replace(br_marker, '\n')

        # Add the cleaned text part if it's not empty
        if text:
            # For list items, we can add a bullet for clarity
            if element.name == 'li':
                text_parts.append(f"• {text}")
            else:
                text_parts.append(text)
    
    final_text = '\n'.join(text_parts)

    final_text = final_text.replace(style_markers['red'][0], '<red>')
    final_text = final_text.replace(style_markers['red'][1], '</red>')
    final_text = final_text.replace(style_markers['darkred'][0], '<darkred>')
    final_text = final_text.replace(style_markers['darkred'][1], '</darkred>')
    final_text = final_text.replace(style_markers['blue'][0], '<blue>')
    final_text = final_text.replace(style_markers['blue'][1], '</blue>')
    final_text = final_text.replace(style_markers['green'][0], '<green>')
    final_text = final_text.replace(style_markers['green'][1], '</green>')

    # Join the parts with a single newline, which creates the desired paragraph structure
    return final_text


def remove_tashkeel(text):
    if not text:
        return ""
    # Regex to match all Arabic diacritics
    tashkeel_pattern = re.compile(r'[\u064B-\u0652\u0670]')
    return re.sub(tashkeel_pattern, '', text)

def read_hadith_count(conn):
    cursor = conn.cursor()
    cursor.execute("SELECT COUNT(*) FROM hadiths")
    count = cursor.fetchone()[0]
    return count

def add_column_if_not_exists(conn, table_name, column_name, column_type):
    cursor = conn.cursor()
    cursor.execute(f"PRAGMA table_info({table_name})")
    columns = [info[1] for info in cursor.fetchall()]
    if column_name not in columns:
        print(f"Column '{column_name}' not found in '{table_name}'. Adding it...")
        cursor.execute(f"ALTER TABLE {table_name} ADD COLUMN {column_name} {column_type}")
        print(f"Column '{column_name}' added successfully.")
    else:
        print(f"Column '{column_name}' already exists in '{table_name}'.")


def process_and_update_hadiths(conn):
    cursor = conn.cursor()

    # 1. Add the new column for search-friendly text
    add_column_if_not_exists(conn, "hadiths", "hadith_normal", "TEXT")

    # 2. Fetch all hadiths that need processing
    cursor.execute("SELECT id, hadith, sharh FROM hadiths")
    all_hadiths = cursor.fetchall()
    
    total_hadiths = len(all_hadiths)
    print(f"\nFound {total_hadiths} hadiths to process.")

#     hadith_id, original_hadith, original_sharh = all_hadiths[1]
#     cleaned_hadith = clean_html_text(original_hadith)
#     cleaned_sharh = clean_html_text(original_sharh)
#     hadith_normal = remove_tashkeel(cleaned_hadith)
        
#     log_print(f"""
# {original_hadith}
# -----------------------------------------------------------------------------------------------------------
# {cleaned_hadith}
# -----------------------------------------------------------------------------------------------------------
# {original_sharh}
# -----------------------------------------------------------------------------------------------------------
# {cleaned_sharh}""")
    # 3. Loop through, process, and update each hadith
    for i, row in enumerate(all_hadiths):
        hadith_id, original_hadith, original_sharh = row

        # Clean the HTML content
        cleaned_hadith = clean_html_text(original_hadith)
        cleaned_sharh = clean_html_text(original_sharh)
        
        # Create the search-friendly version without tashkeel
        hadith_normal = remove_tashkeel(cleaned_hadith)

        # Update the row in the database
        update_query = """
        UPDATE hadiths 
        SET hadith = ?, sharh = ?, hadith_normal = ? 
        WHERE id = ?
        """
        cursor.execute(update_query, (cleaned_hadith, cleaned_sharh, hadith_normal, hadith_id))
        
        # Print progress
        print(f"Processing hadith {i + 1}/{total_hadiths} (ID: {hadith_id})... Done.")

    # 4. Commit all changes to the database
    conn.commit()
    print("\nAll hadiths have been processed and the database has been updated successfully!")

def main():
    original_database_path = "riyad_salheen.db"
    target_database_path = "..\\app\\src\\main\\assets\\databases\\riyad_salheen.db"

    try:
        print(f"Copying '{original_database_path}' to '{target_database_path}'...")
        shutil.copyfile(original_database_path, target_database_path)
        print("Copy complete.")
        
    except IOError as e:
        print(f"Unable to copy file. Error: {e}")
        return
    except Exception as e:
        print(f"An error occurred during copy: {e}")
        return
    
    conn = None
    try:
        print(f"Connecting to processed database: {target_database_path}")
        conn = sqlite3.connect(target_database_path)
        process_and_update_hadiths(conn)
        
    except sqlite3.Error as e:
        print(f"Database error: {e}")
    except Exception as e:
        print(f"An error occurred: {e}")
    finally:
        if conn:
            conn.close()
            print("Processed database connection closed.")

if __name__ == '__main__':
    main()