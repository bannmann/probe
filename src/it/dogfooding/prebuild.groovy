import groovy.xml.XmlUtil

def pluginPom = new File("pom.xml")
def pluginProject = new XmlParser().parseText(pluginPom.text)

def itPom = new File(basedir, "pom.xml")
def itProject = new XmlParser().parseText(itPom.text)
itProject.append(pluginProject.prerequisites[0])
itProject.append(pluginProject.properties[0])
itProject.append(pluginProject.dependencyManagement[0])
itProject.append(pluginProject.dependencies[0])

def writer = new FileWriter(itPom)
XmlUtil.serialize(itProject, writer)

true