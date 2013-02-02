<%@ page import="kangaroo.Term; kangaroo.Setting" %>

%{-- Write some variables into the page so that JavaScript has access to them immediately. --}%
<script type="text/javascript">
    document.Kangaroo = {
        "contextPath":"${request.contextPath}",
        "currentTerm":"${Setting.getSetting("currentTermCode").encodeAsJavaScript()}",
        "defaultSearchTerm":"${Setting.getSetting("defaultSearchTermCode").encodeAsJavaScript()}",
        "terms":{
            ${kangaroo.Term.list().collect { term -> "\"${term.id}\": \"${term.fullDescription}\"" }.join(", ") }
        }
    };

</script>