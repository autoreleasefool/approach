import Dependencies
import SnapshotTesting
import SwiftUI
import XCTest
@testable import AvatarServiceInterface
@testable import AvatarService

final class AvatarViewTests: XCTestCase {
	private static func avatars(ofSize size: AvatarView.Size) -> some View {
		List {
			AvatarView(.text("J", .red()), size: size)
			AvatarView(.data(UIImage.emptyBowlers.pngData()!), size: size)
		}
	}

	func testSmallAvatar() {
		withDependencies {
			$0.avatarService = .liveValue
		} operation: {
			let smallAvatars = Self.avatars(ofSize: .small)

			let vc = UIHostingController(rootView: smallAvatars)
			vc.view.frame = UIScreen.main.bounds

			assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
		}
	}

	func testMediumAvatar() {
		withDependencies {
			$0.avatarService = .liveValue
		} operation: {
			let mediumAvatars = Self.avatars(ofSize: .medium)

			let vc = UIHostingController(rootView: mediumAvatars)
			vc.view.frame = UIScreen.main.bounds

			assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
		}
	}

	func testLargeAvatar() {
		withDependencies {
			$0.avatarService = .liveValue
		} operation: {
			let largeAvatars = Self.avatars(ofSize: .large)

			let vc = UIHostingController(rootView: largeAvatars)
			vc.view.frame = UIScreen.main.bounds

			assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
		}
	}

	func testCustomAvatar() {
		withDependencies {
			$0.avatarService = .liveValue
		} operation: {
			let customAvatars = Self.avatars(ofSize: .custom(42))

			let vc = UIHostingController(rootView: customAvatars)
			vc.view.frame = UIScreen.main.bounds

			assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
		}
	}
}
