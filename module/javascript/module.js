forge['camera'] = {
	'getImage': function (props, success, error) {
		if (typeof props === "function") {
			error = success;
			success = props;
			props = {};
		}
		if (!props) {
			props = {};
		}
		forge.internal.call("camera.getImage", props, success && function (uri) {
			var file = {
				uri: uri,
				name: 'Image',
				type: 'image'
			};
			if (props.width) {
				file.width = props.width;
			}
			if (props.height) {
				file.height = props.height;
			}
			success(file);
		}, error);
	},
	'URL': function (file, success, error) {
		forge.internal.call("camera.URL", file, success, error);
	}
};