import AssetsLibrary
import DateTimeLibrary
import ModelsLibrary
import ModelsViewsLibrary
import ScoreSheetLibrary
import StringsLibrary
import SwiftUI

public struct ShareableGamesImage: View {
	@Environment(\.colorScheme) var colorScheme

	public let configuration: Configuration

	public init(configuration: Configuration) {
		self.configuration = configuration
	}

	public var body: some View {
		VStack(spacing: 0) {
			ForEach(configuration.scores) { game in
				ScoreSheet(
					game: game,
					configuration: .shareable(
						isFirstGame: game.id == configuration.scores.first?.id,
						isLastGame: game.id == configuration.scores.last?.id
					),
					contentSize: CGSize(width: 360, height: 100),
					selection: .constant(.none)
				)
			}
		}
	}
}

extension ShareableGamesImage {
	public struct Configuration: Equatable {
		public let scores: [ScoredGame]
		public let displayScale: CGFloat
		public let colorScheme: ColorScheme

		public init(
			scores: [ScoredGame],
			displayScale: CGFloat,
			colorScheme: ColorScheme
		) {
			self.scores = scores
			self.displayScale = displayScale
			self.colorScheme = colorScheme
		}
	}
}

extension ScoreSheet.Configuration {
	static func shareable(
		isFirstGame: Bool,
		isLastGame: Bool
	) -> ScoreSheet.Configuration {
		ScoreSheet.Configuration(
			foreground: Asset.Colors.ScoreSheet.Text.OnBackground.default,
			foregroundHighlight: Asset.Colors.ScoreSheet.Text.OnBackground.highlight,
			foregroundSecondary: Asset.Colors.ScoreSheet.Text.OnBackground.secondary,
			foregroundFoul: Asset.Colors.ScoreSheet.Text.OnBackground.foul,
			foregroundFoulHighlight: Asset.Colors.ScoreSheet.Text.OnBackground.highlightFoul,
			background: Asset.Colors.ScoreSheet.Background.default,
			backgroundHighlight: Asset.Colors.ScoreSheet.Background.highlight,
			railForeground: Asset.Colors.ScoreSheet.Text.OnRail.default,
			railForegroundHighlight: Asset.Colors.ScoreSheet.Text.OnRail.highlight,
			railBackground: Asset.Colors.ScoreSheet.Rail.default,
			railBackgroundHighlight: Asset.Colors.ScoreSheet.Rail.highlight,
			border: Asset.Colors.ScoreSheet.Border.default,
			allowTopRounding: isFirstGame,
			allowBottomRounding: isLastGame,
			railOnTop: true
		)
	}
}

#Preview(traits: .sizeThatFitsLayout) {
	ShareableGamesImage(configuration: ShareableGamesImage.Configuration(
		scores: (0...4).map {
			ScoredGame(
				id: UUID(uuidString: "00000000-0000-0000-0000-00000000000\($0)")!,
				index: $0,
				frames: Game.FRAME_INDICES.map {
					.init(
						index: $0,
						rolls: [
							.init(index: 0, displayValue: "HP", didFoul: true, isSecondary: false),
							.init(index: 1, displayValue: "10", didFoul: false, isSecondary: false),
							.init(index: 2, displayValue: "-", didFoul: false, isSecondary: false),
						],
						score: 255
					)
				}
			)
		},
		displayScale: .zero,
		colorScheme: .light
	))
}
