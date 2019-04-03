tree = new File(basedir, 'probe-tree.txt')

assert tree.exists()

def artifactOrder = tree.text.findAll(":guava|:metrics-core|:junit|:animal-sniffer-annotations|:jsr305")

/*

This is the structure of the expected tree.

Note that 'animal-sniffer-annotations' is the first dependency of 'ordering', but the second one of 'guava'.

    ordering
        animal-sniffer-annotations
        junit
        metrics-core
        guava
            jsr305
            animal-sniffer-annotations

*/

assert artifactOrder == [":animal-sniffer-annotations", ":junit", ":metrics-core", ":guava", ":jsr305", ":animal-sniffer-annotations"]


return true