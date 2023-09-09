import ModelsLibrary
import ScoreSheetLibrary
import SwiftUI

public enum ShareableError: Error, LocalizedError {
	case failedToGenerateImage
	case failedToGenerateDataFromImage

	public var errorDescription: String? {
		switch self {
		case .failedToGenerateImage:
			return "Failed to generate image"
		case .failedToGenerateDataFromImage:
			return "Failed to generate data from image"
		}
	}
}

public struct ShareableGameSet: Transferable {
	let games: [ScoredGame]
	let configuration: ScoreSheetConfiguration
	let scale: CGFloat

	public static var transferRepresentation: some TransferRepresentation {
		DataRepresentation(exportedContentType: .png) { @MainActor shareable in
			let renderer = ImageRenderer(
				content: CompactScoreSheets(games: shareable.games, configuration: shareable.configuration)
			)

			renderer.scale = shareable.scale

			guard let uiImage = renderer.uiImage else {
				throw ShareableError.failedToGenerateImage
			}

			guard let data = uiImage.pngData() else {
				throw ShareableError.failedToGenerateDataFromImage
			}

			return data
		}
	}
}
