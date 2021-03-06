package kangaroo

import kangaroo.data.convert.ScheduleConvertService
import kangaroo.mn.ProfessorOfficeHours
import kangaroo.mn.Teaching

/**
 * The facilitators of learning, the lifeblood of a university, the people who hate grading.
 */
class Professor {

    def professorService

    static transients = ['name', 'inOfficeHours']

    String id
    String firstName
    String middleName
    String lastName

    /**
     * Has this professor been matched with the faculty data from github.com/austin-college/data?
     * If so -> we will have their photo, title, office location, phone number, and department.
     * If not -> this means they were listed in the course catalog as teaching a class, but not on the austin-college website.
     */
    boolean matched = false

    /**
     * If the professor is currently employed at Austin College.
     * Can be false for retired, fired, or visiting professors who have left.
     */
    boolean isActive = true

    String photoUrl
    String title
    String department
    String office
    String phone
    String email
    String privateEditKey = AppUtils.generateRandomToken()

    String officeNote;  // free text field for note

    // Building the professor's office is associated with.
    Building building;

    // @todo deprecate for "type" enum
    boolean isProfessor = true // True for professors, false for staff.

    static constraints = {
        id(maxSize: 64, blank: false)
        photoUrl(nullable: true, maxSize: 128)
        title(nullable: true, maxSize: 255)
        department(nullable: true, maxSize: 128)
        firstName(maxSize: 64, blank: false)
        middleName(maxSize: 64, blank: false, nullable: true)
        lastName(maxSize: 64, blank: false)
        office(nullable: true, maxSize: 128)
        phone(nullable: true, maxSize: 128)
        email(nullable: true, email: true, maxSize: 128)
        officeNote(nullable: true, maxSize: 255)
        privateEditKey(maxSize: 32, blank: false, unique: true)
        building(nullable: true)
    }

    static mapping = {
        id(column: 'user_id', generator: 'assigned')
    }

    /**
     * Returns a string representation of this professor's name.
     */
    String getName() {
        if (middleName)
            "$firstName $middleName $lastName"
        else
            "$firstName $lastName"
    }

    /**
     * Returns all of the courses this professor is teaching. (Not limited by term)
     */
    List<Course> getCoursesTeaching() { return Teaching.findAllByProfessor(this)*.course }

    /**
     * Returns all of the courses this professor is teaching in the current term.
     */
    List<Course> getCurrentCursesTeaching() { coursesTeaching.findAll { it.term == Term.currentTerm } }

    /**
     * Returns this professor's office hours.
     */
    List<MeetingTime> getOfficeHours(Term term = Term.currentTerm) { return ProfessorOfficeHours.findAllByProfessorAndTerm(this, term)*.meetingTime }

    /**
     * Returns all of the departments this professor teaches classes in (ie, [Biology, Chemistry]).
     */
    List<Department> getActiveDepartments() { professorService.getDepartmentsForProfessor(this) }

    /**
     * Returns all of the rooms this professor teaches classes in (ie, ["MS128", "MS133"]).
     */
    List<String> getActiveRooms() { professorService.getRoomsForProfessor(this) }

    /**
     * Returns similar professors to this one, split by department. (see getRelatedProfessorsForProfessor for more)
     */
    Map<Department, List<Professor>> getRelatedProfessors() { professorService.getRelatedProfessorsForProfessor(this) }

    /**
     * Gets the professor's current status (busy? in office hours? teaching?) as of RIGHT NOW.
     */
    def getStatus() { professorService.getStatus(this) }

    /**
     * Returns true if the professor is having office hours RIGHT NOW.
     */
    boolean isInOfficeHours() { professorService.isInOfficeHours(this) }

    String toString() { name }

    static Professor saveFromJsonObject(object, boolean isProfessor) { // @todo deprecate reliance on isProfessor here
        if (Professor.get(object.id))
            return Professor.get(object.id)

        def professor = new Professor(firstName: object.firstName, middleName: object.middleName, lastName: object.lastName, title: object.title,
                department: object.departmentGroup, isProfessor: isProfessor, email: object.email, office: object.office, phone: object.phone, photoUrl: object.photoURL,
                isActive: object.isActive);

        // First, save the professor.
        professor.id = object.id;
        AppUtils.ensureNoErrors(professor.save());

        // Now save office hours.
        object.officeHours.each { block ->

            def meetingTime = ScheduleConvertService.convertMeetingTime(block).saveOrFind();
            AppUtils.ensureNoErrors(new ProfessorOfficeHours(professor: professor, term: Term.currentTerm, meetingTime: meetingTime).save());
        }

        return professor;
    }

    def toJsonObject() {
        [id: id, firstName: firstName, middleName: middleName, lastName: lastName, type: (isProfessor ? "faculty" : "staff"), title: title,
                departmentGroup: department, email: email, office: office, phone: phone, photoURL: photoUrl,
                isActive: isActive, officeHours: officeHours];
    }
}
