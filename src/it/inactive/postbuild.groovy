tree = new File(basedir, 'probe-tree.txt')

assert tree.exists()

def bexac = tree.text.findAll(".*com.example:bexac:jar:0.8.*")
assert bexac.size == 1
bexac.each {
    assert it.contains('~original~')
}

// com.example:donk is managed to 'provided' scope, so the original node must also appear
assert tree.text.contains("com.example:donk:jar:3.2 ~original~")

// com.example:aipe:jar:1.0 is 'transitively inactive' beneath com.example:bexac:jar:0.8 despite being active elsewhere
assert tree.text.contains("com.example:aipe:jar:1.0 ~inactive~");

// ensure that including inactive dependencies does not result in listing optional ones (e.g. smeezoc -> privil)
assert !tree.text.contains("<optional>");

def inactiveOrOriginal = tree.text.findAll(".*~(inactive|original)~.*")
inactiveOrOriginal.each {
    assert !it.contains('──')
    assert it.contains('╶╶')
}

return true