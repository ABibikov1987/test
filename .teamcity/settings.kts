import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.python
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.buildReportTab

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2020.2"

project {
    description = "Contains all other projects"

    features {
        buildReportTab {
            id = "PROJECT_EXT_1"
            title = "Code Coverage"
            startPage = "coverage.zip!index.html"
        }
    }

    cleanup {
        baseRule {
            preventDependencyCleanup = false
        }
    }

    subProject(Python)
}


object Python : Project({
    name = "python"
    description = "Python"

    buildType(Python_PythonPipline)
})

object Python_PythonPipline : BuildType({
    name = "python_pipline"

    params {
        select("namespase", """"oc"""", label = "namespase", display = ParameterDisplay.PROMPT,
                options = listOf(""""rb"""", """"kb"""", """"oc""""))
        select("stend", "", label = "stend", display = ParameterDisplay.PROMPT,
                options = listOf(""""K3"""", """"K4"""", """"NT"""", """"IFT"""", """"PSI"""", """"PROD""""))
        select("template", "", label = "template", display = ParameterDisplay.PROMPT,
                options = listOf(""""tstr-storage"""", """"tstr-promrtheus"""", """"tstr-keycloack"""", """"tstr-client""""))
        select("shoulder", """"a"""", label = "shoulder", display = ParameterDisplay.PROMPT,
                options = listOf(""""a"""", """"b""""))
        select("release", """"RC-1.1.4"""", label = "release", display = ParameterDisplay.PROMPT,
                options = listOf(""""RC-1.1.4"""", """"master""""))
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        python {
            pythonVersion = customPython {
                executable = """C:\Users\admin\AppData\Local\Programs\Python\Python36\python.exe"""
            }
            command = script {
                content = """
                    stend_path = {'K4': r'C:/tstr-inventory/.teamcity/K4.cfg',
                                  'K3': r'C:/tstr-inventory/.teamcity/K3.cfg',
                                  'NT': r'C:/tstr-inventory/.teamcity/NT.cfg',
                                  'IFT': r'C:/tstr-inventory/.teamcity/IFT.cfg',
                                  'PSI': r'C:/tstr-inventory/.teamcity/PSI.cfg',
                                  'PROD': r'C:/tstr-inventory/.teamcity/PROD.cfg',
                              }
                    
                    config = {}
                    shoulder = %shoulder%
                    namespace = %namespase%
                    path = stend_path[%stend%]
                    with open(path) as f:
                    	for line in f:
                    		line = line.split(':')
                    		try:
                    			key = line[0].split('_')
                    			value = line[1]
                    		except IndexError as error:
                    			continue
                    
                    		if shoulder == key[1] and namespace == key[2]:
                    			prm = key
                    			val = value
                    			if '\n' not in val:
                    				val = value
                    			else:
                    				val = value[:-1]
                    			config[prm[0]] = val
                    print(config)
                """.trimIndent()
            }
        }
    }
})
