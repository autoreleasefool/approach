import Foundation
import SwiftUI

struct AvatarImage: Transferable {
	let data: Data
	let image: UIImage

	static var transferRepresentation: some TransferRepresentation {
		DataRepresentation(importedContentType: .image) { data in
			guard let uiImage = UIImage(data: data) else {
				throw TransferError.importFailed
			}

			return AvatarImage(data: data, image: uiImage)
		}
	}

	enum TransferError: Error {
		case importFailed
	}
}
