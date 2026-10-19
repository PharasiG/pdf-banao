# Sample photos for tests

All four photos are public domain (CC0) on Wikimedia Commons, so they can be kept in this repo.
They were added in task 0.4. Each one was made smaller (to stay under 200 KB) and saved as JPEG
quality 70. All metadata was removed, including GPS location.

| File | Size | Original | Author |
|---|---|---|---|
| `photo_portrait.jpg` | 1125 × 1500 | [All India War Memorial, India Gate in Delhi](https://commons.wikimedia.org/wiki/File:All_India_War_Memorial,_India_Gate_in_Delhi.jpg) | Dudva, CC0 |
| `photo_landscape.jpg` | 1100 × 825 | [Rice cultivation in Jamugurihat Sonitpur Assam India 02](https://commons.wikimedia.org/wiki/File:Rice_cultivation_in_Jamugurihat_Sonitpur_Assam_India_02.jpg) | Nayan j Nath, CC0 |
| `photo_exif_rotate90.jpg` | 933 × 1400 stored, 1400 × 933 shown | [Expo 2010 India Pavilion 01](https://commons.wikimedia.org/wiki/File:Expo_2010_India_Pavilion_01.jpg) | Gary Todd, CC0 |
| `photo_document.jpg` | 1100 × 825 | [Book of Knowledge 1919 Vol 1 Page 110](https://commons.wikimedia.org/wiki/File:Book_of_Knowledge_1919_Vol_1_Page_110.jpg) | Randal Oulton, CC0 |

`photo_exif_rotate90.jpg` is a landscape photo stored turned on its side, with the EXIF
Orientation tag set to 6 ("turn 90° clockwise to show"). Phones save photos like this when they
are held sideways. Code that ignores EXIF will show it sideways.
