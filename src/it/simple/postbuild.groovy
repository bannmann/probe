log = new File(basedir, 'build.log')

assert log.exists()
assert log.text.contains('[INFO] └── com.google.guava:guava:jar:25.1-jre')

return true