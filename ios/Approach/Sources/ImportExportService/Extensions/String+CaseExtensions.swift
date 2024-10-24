import Foundation

extension String {
	var snakeCaseToCamelCase: String {
		lowercased()
			.replacing(/([-_][a-z])/) { match in
				match.output.0.uppercased()
					.replacingOccurrences(of: "_", with: "")
					.replacingOccurrences(of: "-", with: "")
			}
	}
}
