import AssetsLibrary
import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary
@testable import ViewsLibrary

@Suite("EdgeBorder", .tags(.library), .snapshots(record: .failed))
struct EdgeBorderTests {

	@Test("Top border", .tags(.snapshot))
	@MainActor
	func snapshotTopBorder() {
		let content = VStack {
			Text("Top Border")
				.padding()
		}
			.border(edges: [.top])
			.background(.black)

		assertSnapshot(of: content, as: .image)
	}

	@Test("Leading border", .tags(.snapshot))
	@MainActor
	func snapshotLeadingBorder() {
		let content = VStack {
			Text("Leading Border")
				.padding()
		}
			.border(edges: [.leading])
			.background(.black)

		assertSnapshot(of: content, as: .image)
	}

	@Test("Trailing border", .tags(.snapshot))
	@MainActor
	func snapshotTrailingBorder() {
		let content = VStack {
			Text("Trailing Border")
				.padding()
		}
			.border(edges: [.trailing])
			.background(.black)

		assertSnapshot(of: content, as: .image)
	}

	@Test("Bottom border", .tags(.snapshot))
	@MainActor
	func snapshotBottomBorder() {
		let content = VStack {
			Text("Bottom Border")
				.padding()
		}
			.border(edges: [.bottom])
			.background(.black)

		assertSnapshot(of: content, as: .image)
	}

	@Test("All borders", .tags(.snapshot))
	@MainActor
	func snapshotAllBorders() {
		let content = VStack {
			Text("All Borders")
				.padding()
		}
			.border(edges: [.top, .leading, .trailing, .bottom])
			.background(.black)

		assertSnapshot(of: content, as: .image)
	}

	@Test("Border width", .tags(.snapshot))
	@MainActor
	func snapshotBorderWidth() {
		let content = VStack {
			Text("Border width")
				.padding()
		}
			.border(
				edges: [.top, .leading, .trailing, .bottom],
				width: 5
			)
			.background(.black)

		assertSnapshot(of: content, as: .image)
	}

	@Test("Border color", .tags(.snapshot))
	@MainActor
	func snapshotBorderColor() {
		let content = VStack {
			Text("Border color")
				.padding()
		}
			.border(
				edges: [.top, .leading, .trailing, .bottom],
				color: Asset.Colors.Error.default
			)
			.background(.black)

		assertSnapshot(of: content, as: .image)
	}
}
