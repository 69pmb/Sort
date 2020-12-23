$outputDir = "C:\\Users\\Pierre-Marie\\Downloads"
$version = ([XML](Get-Content pom.xml)).project.version

mvn clean install -q -U

Copy-Item -Path ("target\sort-" + $version + "-shaded.jar") -Destination ($outputDir + "\sort.jar")
