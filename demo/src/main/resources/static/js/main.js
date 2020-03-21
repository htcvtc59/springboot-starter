$(document).ready(function () {
    $(".btnProceed").click(function () {
        if ($(".d-none").length > 0) {
            $(".authMethod").removeClass("d-none");
        } else {
            $(".authMethod").addClass("row d-none authMethod");
        }

    });

    $("#btnOTPAuth").click(function () {
        $(".authMethod").removeClass("d-none");
        $(".verifyOTP").removeClass("d-none");
        $(".balance").addClass("d-none");
    });

    $("#btnQRAuth").click(function () {
        $(".authMethod").removeClass("d-none");
        $(".verifyQR").removeClass("d-none");
        $(".balance").addClass("d-none");

        $.ajax({
            url: "/qr/request",
            contentType: "application/json;charset=UTF-8;",
            type: "POST",
            data: JSON.stringify({
                "amount": $("#amount").val(),
                "fromaccount": $("#fromaccount").val(),
                "toaccount": $("#toaccount").val(),
                "effdate": $("#effdate").val(),
            }),
            success: function (response) {
                console.log(response.data);
                if (response.error_code === "SUCCESSFUL" && response.error_message === "SUCCESSFUL") {
                    var data = response.data.split("||");
                    $("#qrauthchallenge").text(data[1]);
                    $("#qrauthtoken").text(data[4]);
                    $("#qrauthdetail").text(data[5]);
                    $("#qrauthcode").text(data[0]);
                    $("#qrplaintext").text(data[6]);

                    // $(".verifyQR img").attr("src",data[2]);
                    var qrcode = new QRCode(document.getElementById("qrcode"), {
                        text: data[0],
                        width: 300,
                        height: 300,
                        colorDark: "#000000",
                        colorLight: "#ffffff",
                        correctLevel: QRCode.CorrectLevel.H
                    });

                } else if (response.error_code === "FAILED" && response.error_message === "FAILED") {
                    $(".notifi-body").text(response.error_message);
                    $(".notification").modal("show");
                    setTimeout(function () {
                        document.location.reload(true);
                    }, 1500)
                }
            }
        });

        setInterval(function () {
            $.ajax({
                url: "/qr/statecheck",
                contentType: "application/json;charset=UTF-8;",
                type: "POST",
                data: JSON.stringify({
                    "authToken": $("#qrauthtoken").text()
                }),
                success: function (response) {
                    console.log(response)
                    if (response.error_code === "SUCCESSFUL" && response.error_message === "SUCCESSFUL") {
                        $(".notifi-body").text(response.error_message);
                        $(".notification").modal("show");
                        setTimeout(function () {
                            document.location.reload(true);
                        }, 1500)
                    } else if (response.error_code === "FAILED" && response.error_message === "FAILED") {
                        $(".notifi-body").text(response.error_message);
                        $(".notification").modal("show");
                        setTimeout(function () {
                            document.location.reload(true);
                        }, 1500)
                    }

                }
            })
        }, 1000);

    });

    $(".btnOTPAuthSubmit").click(function () {
        $.ajax({
            url: "/otp/auth",
            contentType: "application/json;charset=UTF-8;",
            type: "POST",
            data: JSON.stringify({
                "amount": $("#amount").val(),
                "fromaccount": $("#fromaccount").val(),
                "toaccount": $("#toaccount").val(),
                "effdate": $("#effdate").val(),
                "otp": $("#otp").val()
            }),
            success: function (response) {
                if (response.error_code === "SUCCESSFUL" && response.error_message === "SUCCESSFUL") {
                    $(".notifi-body").text(response.error_message);
                    $(".notification").modal("show");
                } else if (response.error_code === "FAILED" && response.error_message === "FAILED") {
                    $(".notifi-body").text(response.error_message);
                    $(".notification").modal("show");
                }
                setTimeout(function () {
                    document.location.reload(true)
                }, 1500)

            }
        });


    })

    $("#btnCROTPAuth").click(function () {
        $(".authMethod").removeClass("d-none");
        $(".verifyCROTP").removeClass("d-none");
        $(".balance").addClass("d-none");

        $.ajax({
            url: "/crotp/request",
            contentType: "application/json;charset=UTF-8;",
            type: "POST",
            success: function (response) {
                if (response.error_code === "SUCCESSFUL" && response.error_message === "SUCCESSFUL") {
                    $("#challenge").text(response.data);

                } else if (response.error_code === "FAILED" && response.error_message === "FAILED") {
                    $(".notifi-body").text(response.error_message);
                    $(".notification").modal("show");
                    setTimeout(function () {
                        document.location.reload(true);
                    }, 1500)
                }

            }
        });

    });

    $(".btnCROTPAuthSubmit").click(function () {
        $.ajax({
            url: "/crotp/auth",
            contentType: "application/json;charset=UTF-8;",
            type: "POST",
            data: JSON.stringify({
                "challenge": $("#challenge").text(),
                "crotp": $("#crotp").val()
            }),
            success: function (response) {
                if (response.error_code === "SUCCESSFUL" && response.error_message === "SUCCESSFUL") {
                    $(".notifi-body").text(response.error_message);
                    $(".notification").modal("show");
                    setTimeout(function () {
                        document.location.reload(true);
                    }, 1500)
                } else if (response.error_code === "FAILED" && response.error_message === "FAILED") {
                    $(".notifi-body").text(response.error_message);
                    $(".notification").modal("show");
                    setTimeout(function () {
                        document.location.reload(true);
                    }, 1500)
                }

            }
        })

    });

    $(".btnQROTPAuthSubmit").click(function () {
        var detail = $("#qrauthdetail").text();
        var token = $("#qrauthtoken").text();
        var challenge = $("#qrauthchallenge").text();
        var qrcode = $("#qrauthcode").text();
        var qrotp = $("#qrotp").val();
        var qrplaintext = $("#qrplaintext").text();
        console.log(detail + "\n" + token + "\n" + challenge + "\n" + qrcode + "\n" + qrotp + "\n" + qrplaintext)

        $.ajax({
            url: "/qr/auth",
            contentType: "application/json;charset=UTF-8;",
            type: "POST",
            data: JSON.stringify({
                "detail": detail,
                "token": token,
                "challenge": challenge,
                "qrcode": qrcode,
                "qrotp": qrotp,
                "qrplaintext": qrplaintext
            }),
            success: function (response) {
                console.log(response)


            }
        })


    })


});