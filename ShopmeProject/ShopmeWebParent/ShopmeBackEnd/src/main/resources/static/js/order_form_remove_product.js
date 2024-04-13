var productDetailCount;
$(document).ready(function() {

    productDetailCount = $(".hiddenProductId").length;

    $("#productList").on("click", ".linkRemove", function(e) {
        e.preventDefault();

        if(doesOrderHaveOnlyOneProduct()) {
            showWarningModal("You must have at least one product in your order.");
        } else {
            removeProduct($(this));
            updateOrderAmounts();
        }
    });
});

function removeProduct(link) {
    rowNumber = link.attr("rowNumber");
    $("#row" + rowNumber).remove();
    $("#blankLine" + rowNumber).remove();

    productDetailCount--;
    $(".divCount").each(function(index, element) {
       element.innerHTML = "" + (index + 1);
    });
}

function doesOrderHaveOnlyOneProduct() {
    productCount = $(".hiddenProductId").length;
    return productCount == 1;
}