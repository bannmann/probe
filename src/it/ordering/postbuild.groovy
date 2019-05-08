tree = new File(basedir, 'probe-tree.txt')

assert tree.exists()

def artifactOrder = tree.text.findAll("(?<=com.example:)[a-z]+")

/*

This is the structure of the expected tree.

Note that 'kesh' is the first dependency of 'ordering', but the second one of 'niranka'.
Also, the ordering of 'ginexi' dependencies is non-alphabetical.

    ordering
        kesh
        ginexi
            donk
            bexac
        aipe
        niranka
            privil
            kesh

*/

assert artifactOrder == ["kesh", "ginexi", "donk", "bexac", "aipe", "niranka", "privil", "kesh"]

return true