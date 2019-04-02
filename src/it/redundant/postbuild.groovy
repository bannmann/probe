tree = new File(basedir, 'probe-tree.txt')

assert tree.exists()

def occurrences(haystack, needle) {
    return haystack.split(needle, -1).length - 1
}

assert occurrences(tree.text, 'io.netty:netty-transport:jar:4.0.44.Final') == 2

return true