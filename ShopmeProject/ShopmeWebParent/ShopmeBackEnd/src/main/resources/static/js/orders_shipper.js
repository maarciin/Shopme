var iconNames = {
    'PICKED': 'fa-people-carry', 'SHIPPING': 'fa-shipping-fast', 'DELIVERED': 'fa-box-open', 'RETURNED': 'fa-undo'
};
var confirmText;
var confirmModalDialog;
var yesButton;
var noButton;

$(document).ready(function () {
    confirmText = $("#confirmText");
    confirmModalDialog = $("#confirmModal");
    yesButton = $("#yesButton");
    noButton = $("#noButton");
    $(".linkUpdateStatus").on("click", function (e) {
        e.preventDefault();
        link = $(this);
        shopUpdateConfirmModal(link);
    });

    addEventHandlerForYesButton();
});

function addEventHandlerForYesButton() {
    yesButton.click(function (e) {
        e.preventDefault();
        sendRequestToUpdateOrder($(this));
    });
}

function sendRequestToUpdateOrder(button) {
    url = button.attr("href");

    $.ajax({
        type: "POST", url: url, beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
    }).done(function (response) {
        showMessageModal("Order has been updated successfully!");
        updateStatusIconColor(response.orderId, response.status);
    }).fail(function () {
        showMessageModal("Error updating order status");
    })
}

function updateStatusIconColor(orderId, status) {
    link = $("#link" + status + orderId);
    link.replaceWith("<i class='fas " + iconNames[status] + " fa-2x icon-green'></i>");
}

function shopUpdateConfirmModal(link) {
    noButton.text("NO");
    yesButton.show();

    orderId = link.attr("orderId");
    status = link.attr("status");
    yesButton.attr("href", link.attr("href"));

    confirmText.text("Are you sure your want to update order with ID #" + orderId + " to status: " + status + "?");

    confirmModalDialog.modal();
}

function showMessageModal(message) {
    noButton.text("Close");
    yesButton.hide();
    confirmText.text(message);
}