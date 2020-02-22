function deleteDepartmentModal(departmentId) {
    $("#deleteDepartmentButton").attr("href", "/department/delete/" + departmentId);
}

function deleteLocationModal(locationId) {
    $("#deleteLocationButton").attr("href", "/location/delete/" + locationId);
}

function deleteEmployeeModal(employeeId) {
    $("#deleteEmployeeButton").attr("href", "/employee/delete/" + employeeId);
}

$(document).ready(function(){
    $("#search").on("keyup", function() {
        let value = $(this).val().toLowerCase();
        $("#table tr").filter(function() {
            $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
        });
    });
});