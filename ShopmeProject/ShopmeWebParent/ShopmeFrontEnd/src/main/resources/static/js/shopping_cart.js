$(document).ready(function () {
    $(".linkMinus").on("click", function (e) {
        e.preventDefault();
        decreaseQuantity($(this));

    });

    $(".linkPlus").on("click", function (e) {
        e.preventDefault();
        increaseQuantity($(this));
    });
});

function decreaseQuantity(link) {
    productId = link.attr("pid");
    quantityInput = $("#quantity" + productId);
    newQuantity = parseInt(quantityInput.val()) - 1;
    if (newQuantity > 0) {
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    } else {
        showWarningModal("You must choose at least one item.");
    }
}

function increaseQuantity(link) {
    productId = link.attr("pid");
    quantityInput = $("#quantity" + productId);
    newQuantity = parseInt(quantityInput.val()) + 1;
    if (newQuantity <= 5) {
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    } else {
        showWarningModal("You can only buy a maximum of 5 items.");
    }
}

function updateQuantity(productId, quantity) {
    url = contextPath + "cart/update/" + productId + "/" + quantity;

    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (updatedSubtotal) {
        updateSubtotal(updatedSubtotal, productId);
        updateTotal();
    }).fail(function () {
        showErrorModal("Error while updating product to cart!");
    });
}

function updateSubtotal(updatedSubtotal, productId) {
    formattedSubtotal = $.number(updatedSubtotal, 2);
    $("#subtotal" + productId).text(updatedSubtotal);
}

function updateTotal() {
    total = 0.0;

    $(".subtotal").each(function (index, element) {
        total += parseFloat(element.innerHTML.replaceAll(",", ""));
    });

    formattedTotal = $.number(total, 2);
    $("#total").text(formattedTotal);
}