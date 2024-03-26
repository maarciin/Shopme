decimalSeparator = decimalPointType === 'COMMA' ? ',' : '.';
thousandSeparator = thousandsPointType === 'COMMA' ? ',' : '.';

$(document).ready(function () {
    $(".linkMinus").on("click", function (e) {
        e.preventDefault();
        decreaseQuantity($(this));

    });
    $(".linkPlus").on("click", function (e) {
        e.preventDefault();
        increaseQuantity($(this));
    });

    $(".linkRemove").on("click", function (e) {
        e.preventDefault();
        removeProduct($(this));
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
        showWarningModal("You can only buy a maximum of 5 items of the same product.");
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
    $("#subtotal" + productId).text(formatCurrency(updatedSubtotal));
}

function updateTotal() {
    total = 0.0;
    productCount = 0;

    $(".subtotal").each(function (index, element) {
        productCount++;
        total += parseFloat(clearCurrencyFormat(element.innerHTML));
    });

    if (productCount < 1) {
        showEmptyShoppingCart();
    } else {
        $("#total").text(formatCurrency(total));
    }
}

function showEmptyShoppingCart() {
    $("#sectionTotal").hide();
    $("#sectionEmptyCartMessage").removeClass("d-none");
}

function removeProduct(link) {
    url = link.attr("href");

    $.ajax({
        type: "DELETE",
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (reponse) {
        rowNumber = link.attr("rowNumber");
        removeProductHTML(rowNumber);
        updateTotal();
        updateCountNumbers();
        showModalDialog("Shopping Cart", reponse);
    }).fail(function () {
        showErrorModal("Error while removing product!");
    });
}

function removeProductHTML(rowNumber) {
    $("#row" + rowNumber).remove();
    $("#blankLine" + rowNumber).remove();
}

function updateCountNumbers() {
    $(".divCount").each(function (index, element) {
        element.innerHTML = index + 1;
    });
}

function formatCurrency(amount) {
    return $.number(amount, decimalDigits, decimalSeparator, thousandSeparator);
}

function clearCurrencyFormat(numberString) {
    result = numberString.replaceAll(thousandSeparator, "");
    return result.replaceAll(decimalSeparator, ".");
}