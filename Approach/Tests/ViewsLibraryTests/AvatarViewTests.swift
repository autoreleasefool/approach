import SnapshotTesting
import SwiftUI
import XCTest
@testable import ViewsLibrary

final class AvatarViewTests: XCTestCase {
	private static func avatars(ofSize size: AvatarView.Size) -> some View {
		List {
			AvatarView(size: size)
			AvatarView(.emptyBowlers, size: size)
			AvatarView(size: size, title: "Joseph Roque")
			AvatarView(size: size, subtitle: "Joseph Roque")
			AvatarView(size: size, title: "Joseph Roque", subtitle: "Skyview Lanes")
		}
	}

	func testSmallAvatar() {
		let smallAvatars = Self.avatars(ofSize: .small)

		let vc = UIHostingController(rootView: smallAvatars)
		vc.view.frame = UIScreen.main.bounds

		// FIXME: app colors not rendering
		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}

	func testMediumAvatar() {
		let mediumAvatars = Self.avatars(ofSize: .medium)

		let vc = UIHostingController(rootView: mediumAvatars)
		vc.view.frame = UIScreen.main.bounds

		// FIXME: app colors not rendering
		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}

	func testLargeAvatar() {
		let largeAvatars = Self.avatars(ofSize: .large)

		let vc = UIHostingController(rootView: largeAvatars)
		vc.view.frame = UIScreen.main.bounds

		// FIXME: app colors not rendering
		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}

	func testCustomAvatar() {
		let customAvatars = Self.avatars(ofSize: .custom(42))

		let vc = UIHostingController(rootView: customAvatars)
		vc.view.frame = UIScreen.main.bounds

		// FIXME: app colors not rendering
		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
