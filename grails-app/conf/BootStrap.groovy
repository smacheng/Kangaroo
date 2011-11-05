import coursesearch.Course
import coursesearch.Term
import grails.converters.JSON
import grails.util.Environment
import coursesearch.Professor

class BootStrap {

    // Services to initialize our data.
    def departmentDataService
    def courseImporterService
    def facultyDataService
    def textbookDataService
    def cacheService

    def init = { servletContext ->

        println "\n\n==============================\n\n    Kangaroo v1 starting..."

        // Customize how objects are formatted to JSON.
        JSON.registerObjectMarshaller(Course) {
            return [id: it.id, name: it.name, items: it.items];
        }

        if (Environment.current != Environment.TEST) {
            departmentDataService.setUpDepartments()

            if (Professor.count() == 0)
                facultyDataService.fetchAndMatch()

            // Create terms and import courses.
            if (Term.count() == 0) {
                ["11FA", "12SP"].each {
                    def term = Term.findOrCreate(it)

                    if (Environment.current == Environment.DEVELOPMENT) {
                        println "Downloading course files..."
                        courseImporterService.importFromJson(term, new URL("http://phillipcohen.net/accourses/courses_${term.shortCode}.json").text)
                    }
                }

                textbookDataService.lookupTextbooksForAllCourses()
            }

//            cacheService.initializeCache()
        }

        println "\n==============================\n"
    }

    def destroy = {
    }
}
