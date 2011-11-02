import coursesearch.Course
import coursesearch.Textbook
import grails.converters.JSON
import grails.util.Environment
import coursesearch.Term

class BootStrap {

    // Services to initialize our data.
    def departmentDataService
    def courseDataService
    def facultyDataService
    def textbookDataService
    def cacheService

    def init = { servletContext ->

        // Customize how objects are formatted to JSON.
        JSON.registerObjectMarshaller(Course) {
            return [id: it.id, name: it.name, items: it.items];
        }

        // Create terms.
        def terms = ["11FA", "12SP"]
        terms.each { Term.findOrCreate(it) }

        if (Environment.current != Environment.TEST) {
            departmentDataService.setUpDepartments()
            if (Course.count() == 0)
                courseDataService.downloadAndProcess();
//            if (Textbook.count() == 0)
//                textbookDataService.lookupTextbooksForAllCourses();
            facultyDataService.fetchAndMatch()

            // Pre-cache as much information as we can.
            cacheService.initializeCache();
        }
    }

    def destroy = {
    }
}
