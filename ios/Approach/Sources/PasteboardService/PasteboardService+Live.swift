import Dependencies
import PasteboardServiceInterface
import UIKit

extension PasteboardService: DependencyKey {
	public static var liveValue: Self = {
		return Self(
			copyToClipboard: { value in
				UIPasteboard.general.string = value
			}
		)
	}()
}
