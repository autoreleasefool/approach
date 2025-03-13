//
//  Tags.swift
//  Approach
//
//  Created by Joseph Roque on 2025-03-07.
//

import Testing

extension Tag {
	// Categories
	@Tag public static var android: Self
	@Tag public static var extensions: Self
	@Tag public static var imports: Self
	@Tag public static var statistics: Self
	@Tag public static var strings: Self

	// Test Kind
	@Tag public static var snapshot: Self
	@Tag public static var performance: Self
	@Tag public static var unit: Self

	// Module Type
	@Tag public static var feature: Self
	@Tag public static var library: Self
	@Tag public static var repository: Self
	@Tag public static var service: Self

	// Dependencies
	@Tag public static var grdb: Self
	@Tag public static var dependencies: Self
	@Tag public static var tca: Self
}
