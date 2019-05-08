tree = new File(basedir, 'probe-tree.txt')

assert tree.exists()


def occurrences(haystack, needle) {
    return haystack.split(needle, -1).length - 1
}

assert occurrences(tree.text, 'com.example:kesh:jar:2.6.0') == 2
assert occurrences(tree.text, 'com.example:kesh:jar') == 2
assert tree.text.contains('com.example:kesh:jar:2.6.0 (version managed from 2.6.1)')

assert tree.text.contains("com.example:bexac:jar:0.8 <optional> (optional managed from false)");
assert tree.text.contains("com.example:privil:jar:2.0 (optional managed from true)");

return true