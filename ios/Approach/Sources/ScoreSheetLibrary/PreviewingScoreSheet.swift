import AssetsLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct PreviewingScoreSheet: View {
	let style: ScoreSheetConfiguration.Style
	private static let rolls = ["L", "/", "8"]

	@State private var headerWidth: CGFloat = .zero
	@State private var rowHeight: CGFloat = .zero
	@State private var frameWidth: CGFloat = .zero

	public init(style: ScoreSheetConfiguration.Style) {
		self.style = style
	}

	public var body: some View {
		Grid(alignment: .leading, horizontalSpacing: 0, verticalSpacing: 0) {
			GridRow {
				Text("")
					.frame(width: headerWidth)

				Text("1")
					.font(.caption2)
					.padding(.unitSpacing)
					.foregroundColor(style.textOnRail)
					.matchWidth(byKey: FrameWidthKey.self, to: $frameWidth)
					.background(style.railBackground)
					.roundCorners(topLeading: true)
			}

			GridRow {
				Text(Strings.Game.titleWithOrdinal(1))
					.font(.caption)
					.minimumScaleFactor(0.2)
					.lineLimit(1)
					.foregroundColor(style.textOnBackground)
					.padding(.smallSpacing)
					.matchWidth(byKey: GameHeaderWidthKey.self, to: $headerWidth)
					.matchHeight(byKey: RowHeightKey.self, to: $rowHeight)
					.background(style.background)
					.roundCorners(topLeading: true)

				Grid(horizontalSpacing: 0, verticalSpacing: 0) {
					GridRow {
						ForEach(Self.rolls, id: \.self) { roll in
							Text(roll)
								.font(.caption2)
								.minimumScaleFactor(0.2)
								.lineLimit(1)
								.padding(.unitSpacing)
								.foregroundColor(style.textOnBackground)
								.border(edges: [.trailing, .bottom], color: style.border)
						}
					}

					GridRow {
						Text("23")
							.font(.caption)
							.padding(.smallSpacing)
							.foregroundColor(style.textOnBackground)
							.gridCellColumns(Frame.NUMBER_OF_ROLLS)
					}
				}
				.matchHeight(byKey: RowHeightKey.self, to: $rowHeight)
				.matchWidth(byKey: FrameWidthKey.self, to: $frameWidth)
				.background(style.background)
			}
		}
		.dynamicTypeSize(.medium)
		.border(edges: [.trailing, .bottom], width: .standardRadius * 2, color: style.strongBorder)
	}
}

private struct GameHeaderWidthKey: PreferenceKey, MatchDimensionPreferenceKey {}
private struct FrameWidthKey: PreferenceKey, MatchDimensionPreferenceKey {}
private struct RowHeightKey: PreferenceKey, MatchDimensionPreferenceKey {}

#if DEBUG
struct PreviewingShareableScoreSheetPreviews: PreviewProvider {
	static var previews: some View {
		Grid(horizontalSpacing: .standardSpacing, verticalSpacing: .standardSpacing) {
			GridRow {
				PreviewingScoreSheet(style: .plain)
				PreviewingScoreSheet(style: .default)
				PreviewingScoreSheet(style: .pride)
			}

			GridRow {
				PreviewingScoreSheet(style: .default)
				PreviewingScoreSheet(style: .plain)
				PreviewingScoreSheet(style: .pride)
			}

			GridRow {
				PreviewingScoreSheet(style: .plain)
				PreviewingScoreSheet(style: .default)
				PreviewingScoreSheet(style: .pride)
			}
		}
		.padding()
	}
}
#endif
