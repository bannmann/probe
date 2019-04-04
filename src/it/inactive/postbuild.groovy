tree = new File(basedir, 'probe-tree.txt')

assert tree.exists()

def nettyCommon = tree.text.findAll(".*io.netty:netty-common:jar:4.1.9.Final.*")
assert nettyCommon.size == 6
nettyCommon.each {
    assert it.contains('~original~') || it.contains('~inactive~')
}

// netty-common 4.0.44.Final is 'transitively inactive' beneath netty-codec 4.0.44.Final despite being active elsewhere
assert tree.text.contains("io.netty:netty-common:jar:4.0.44.Final ~inactive~");

def inactiveOrOriginal = tree.text.findAll(".*~(inactive|original)~.*")
inactiveOrOriginal.each {
    assert !it.contains('──')
    assert it.contains('╶╶')
}

return true