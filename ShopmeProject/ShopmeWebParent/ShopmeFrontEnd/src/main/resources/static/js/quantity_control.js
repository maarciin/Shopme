$(document).ready(function() {
    $(".linkMinus").on("click", function(e) {
        e.preventDefault();
        productId = $(this).attr("pid");
        quantityInput = $("#quantity" + productId);
        newQuantity = parseInt(quantityInput.val()) - 1;
        if (newQuantity > 0) {
            quantityInput.val(newQuantity);
        } else {
            showWarningModal("You must choose at least one item.");
        }
    });

    $(".linkPlus").on("click", function(e) {
        e.preventDefault();
        productId = $(this).attr("pid");
        quantityInput = $("#quantity" + productId);
        newQuantity = parseInt(quantityInput.val()) + 1;
        if (newQuantity <= 5) {
            quantityInput.val(newQuantity);
        } else {
            showWarningModal("You can only buy a maximum of 5 items.");
        }
    });
});