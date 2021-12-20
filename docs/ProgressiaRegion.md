# Progressia Region File
## Description
The `.progressia_region` file type is used for all region files in the game Progressia. Each region file contains a cube of 16x16x16 chunks.
## Header
The header of the file is 16&nbsp;400 bytes. Every file starts with the string byte sequence `\x50\x52\x4F\x47` (UTF-8 for `PROG`), followed with the three integer values of the region position, in region coordinates. After this, there is exactly 16KiB of space in the header, which stores the offsets to the chunks' data. This space holds an integer, 4 bytes, for each chunk in the region. The integer value starts at 0 for every chunk, and is changed to the location of the chunk data once created. These are indexed in order by flattening the 3D in-chunk coordinates into a number between 0 and 4095 according to the formula `offset = 256*x+ 16*y + z` for chunk at (x, y, z). To convert from this offset value to the offset in bytes, use `byte_offset = 16400 + 64*n`.
## Sectors
Sectors are what is used to store chunk data, and are not linear, but are followed until they reach an ending block. Each is 64 bytes, which is used in the header section to find the byte offset. Each sector starts with a identification byte, followed by the sector data.

 0. Ending - This sector is empty, and marks the end of the chunk data (This may change in the future.
 1. Data - This sector contains chunk data for a single chunk. The second byte of this sector contains a counter byte, which is a form of "checksum" to make sure that the program is reading the proper sectors in order. This starts at 0 for the first data sector and increments by one for each new data sector.
 2. Partition Link - This sector only contains another offset value, which is where the next sector is. This allows for infinite chunk size, avoiding "chunk dupes" as were present in Minecraft without reverting any chunks.
 3. Bulk Data - These would be used for many chunks in the same region that contain exactly the same data, e.g. all solid chunks underground. Exists so the program knows not to overwrite them, and just make new chunk data if modified. Not yet implemented.