tree = new File(basedir, 'probe-tree.txt')

assert tree.exists()

def occurrences(haystack, needle) {
    return haystack.split(needle, -1).length - 1
}

assert occurrences(tree.text, 'com.example:privil') == 2

return true