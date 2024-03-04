@ECHO OFF

java -jar dist/software_renderer.jar -cli -pack_texture config/textures.txt -pack_mesh config/meshes.txt
REM java -jar dist/software_renderer.jar -cli -pack_texture config/textures.txt
REM java -jar dist/software_renderer.jar -cli -pack_mesh config/meshes.txt