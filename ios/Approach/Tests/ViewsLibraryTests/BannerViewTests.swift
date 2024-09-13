import SnapshotTesting
import SwiftUI
import Testing
@testable import ViewsLibrary

struct BannerViewTests {
	@Test("Banner snapshots", .tags(.snapshot))
	@MainActor func snapshotBanners() {
		let badges = VStack {
			VStack(alignment: .leading) {
				Banner(.title("Banner"), style: .plain)
				Banner(.title("Banner"), style: .success)
				Banner(.title("Banner"), style: .destructive)
				Banner(.title("Banner"), style: .info)
				Banner(.title("Banner"), style: .primary)
				Banner(.title("Banner"), style: .init(foreground: .red, background: .blue))
			}
			.frame(maxWidth: .infinity)

			VStack(alignment: .leading) {
				Banner(.titleAndMessage("Banner", "This is a message"), style: .plain)
				Banner(.titleAndMessage("Banner", "This is a message"), style: .success)
				Banner(.titleAndMessage("Banner", "This is a message"), style: .destructive)
				Banner(.titleAndMessage("Banner", "This is a message"), style: .info)
				Banner(.titleAndMessage("Banner", "This is a message"), style: .primary)
				Banner(.titleAndMessage("Banner", "This is a message"), style: .init(foreground: .red, background: .blue))
			}
			.frame(maxWidth: .infinity)
		}
		.frame(width: 428)

		assertSnapshot(of: badges, as: .image)
	}
}
