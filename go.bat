set PATH_TO_FX=C:\dev\bin\javafx-sdk-15.0.1\lib
set PATH_TO_FX_current_build=C:\dev\bin\javafx-jcurrent_build-15.0.1

rmdir /Q /S current_build

dir /s /b src\*.java > sources.txt & javac --module-path %PATH_TO_FX% -d current_build/nullinside @sources.txt & del sources.txt
xcopy src\main\resources\ current_build\nullinside /S /I
java --module-path "%PATH_TO_FX%;current_build" -m org.nullinside/org.nullinside.App