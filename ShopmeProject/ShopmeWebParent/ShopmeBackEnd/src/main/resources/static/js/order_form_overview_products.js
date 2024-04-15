var fieldProductCost;
var fieldSubtotal;
var fieldShippingCost;
var fieldTax;
var fieldTotal;

$(document).ready(function () {

    fieldProductCost = $("#productCost");
    fieldSubtotal = $("#subtotal");
    fieldShippingCost = $("#shippingCost");
    fieldTax = $("#tax");
    fieldTotal = $("#total");

    formatOrderAmounts();
    formatProductAmounts();

    $("#productList").on("change", ".quantity-input", function (e) {
        updateSubtotalWhenQuantityChanged($(this));
        updateOrderAmounts();
    });

    $("#productList").on("change", ".price-input", function (e) {
        updateSubtotalWhenPriceChanged($(this));
        updateOrderAmounts();
    });
    $("#productList").on("change", ".cost-input", function (e) {
        updateOrderAmounts();
    });
    $("#productList").on("change", ".ship-input", function (e) {
        updateOrderAmounts();
    });
})

function updateOrderAmounts() {
    totalCost = 0.0;

    $(".cost-input").each(function (e) {
        costInputField = $(this);
        rowNumber = costInputField.attr("rowNumber");
        quantityValue = $("#quantity" + rowNumber).val();

        productCost = parseFloat(costInputField.val().replace(",", "."));
        totalCost += productCost * parseInt(quantityValue);
    });

    setAndFormatNumberForField("productCost", totalCost);

    orderSubtotal = 0.0;
    $(".subtotal-output").each(function (e) {
        subtotalOutputField = $(this);

        subTotalValue = parseFloat(subtotalOutputField.val().replace(",", "."));
        orderSubtotal += subTotalValue;

    });

    setAndFormatNumberForField("subtotal", orderSubtotal);

    shippingCost = 0.0;
    $(".ship-input").each(function (e) {
        shipInputField = $(this);

        shipInputValue = parseFloat(shipInputField.val().replace(",", "."));
        shippingCost += shipInputValue;

    });

    setAndFormatNumberForField("shippingCost", shippingCost);

    tax = parseFloat(fieldTax.val().replace(",", "."));
    orderTotal = orderSubtotal + tax + shippingCost;

    setAndFormatNumberForField("total", orderTotal);
}

function updateSubtotalWhenQuantityChanged(input) {
    quantityValue = input.val();
    rowNumber = input.attr("rowNumber");
    priceField = $("#price" + rowNumber);
    priceValue = parseFloat(priceField.val().replace(",", "."));
    newSubtotal = parseFloat(quantityValue) * priceValue;

    setAndFormatNumberForField("subtotal" + rowNumber, newSubtotal);
}

function updateSubtotalWhenPriceChanged(input) {
    priceValue = input.val().replace(",", ".");
    rowNumber = input.attr("rowNumber");
    quantityField = $("#quantity" + rowNumber);
    quantityValue = quantityField.val();
    newSubtotal = parseFloat(priceValue) * quantityValue;

    setAndFormatNumberForField("subtotal" + rowNumber, newSubtotal);
}

function setAndFormatNumberForField(fieldId, fieldValue) {
    formattedValue = $.number(fieldValue, 2);
    $("#" + fieldId).val(formattedValue);
}

function formatOrderAmounts() {
    formatNumberForField(fieldProductCost);
    formatNumberForField(fieldSubtotal);
    formatNumberForField(fieldShippingCost);
    formatNumberForField(fieldTax);
    formatNumberForField(fieldTotal);
}

function formatProductAmounts() {
    $(".cost-input").each(function (e) {
        formatNumberForField($(this));
    });
    $(".price-input").each(function (e) {
        formatNumberForField($(this));
    });
    $(".subtotal-output").each(function (e) {
        formatNumberForField($(this));
    });
    $(".ship-input").each(function (e) {
        formatNumberForField($(this));
    });
}

function formatNumberForField(fieldRef) {
    fieldRef.val($.number(fieldRef.val(), 2));
}

function processFormBeforeSubmit() {
    setCountryName();
    removeThousandSeparatorForField(fieldProductCost);
    removeThousandSeparatorForField(fieldSubtotal);
    removeThousandSeparatorForField(fieldShippingCost);
    removeThousandSeparatorForField(fieldTax);
    removeThousandSeparatorForField(fieldTotal);

    $(".cost-input").each(function (e) {
        removeThousandSeparatorForField($(this));
    });

    $(".price-input").each(function (e) {
        removeThousandSeparatorForField($(this));
    });

    $(".subtotal-output").each(function (e) {
        removeThousandSeparatorForField($(this));
    });

    $(".ship-input").each(function (e) {
        removeThousandSeparatorForField($(this));
    });

    return true;
}

function removeThousandSeparatorForField(fieldRef) {
    fieldRef.val(fieldRef.val().replace(",", "."));
}

function setCountryName() {
    selectedCountry = $("#country option:selected");
    countryName = selectedCountry.text();
    $("#countryName").val(countryName);

}