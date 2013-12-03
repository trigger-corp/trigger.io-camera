if (forge.is.android()) {
	module('camera');

	asyncTest("Camera", 1, function() {
		askQuestion("Does this device have a camera?<br>If yes use the camera to take a picture when given the option", {"I have a camera": function () {
			forge.camera.getImage({width: 200, height: 200}, function (file) {
				forge.camera.URL(file, function (url) {
					askQuestion("Is this your image:<br><img src='"+url+"'>", {"Yes": function () {
						ok(true, "User claims success");
						start();
					}, "No": function () {
						ok(false, "User claims failure");
						start();
					}});
				});			
			});
		}, "No camera": function () {
			ok(false, "No camera available");
			start();
		}});
	});
}

;
