import Algorithms
import AssetsLibrary
import ComposableArchitecture
import Foundation
import ModelsLibrary
import ScoreSheetLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

@ViewAction(for: Sharing.self)
public struct SharingView: View {
	@Bindable public var store: StoreOf<Sharing>

	@Environment(\.displayScale) var displayScale

	public init(store: StoreOf<Sharing>) {
		self.store = store
	}

	public var body: some View {
		VStack(spacing: 0) {
			preview

			List {
				styleOptions
				frameOptions
				labelOptions
				layoutOptions
			}

			Divider()

			shareButtons
		}
		.navigationTitle(store.navigationTitle)
		.navigationBarTitleDisplayMode(.inline)
		.toolbar {
			ToolbarItem(placement: .navigationBarLeading) {
				Button(Strings.Action.done) { send(.didTapDoneButton) }
			}
		}
		.onAppear { send(.onAppear) }
		.onFirstAppear { send(.didFirstAppear) }
		.onFirstAppear { send(.didUpdateDisplayScale(displayScale)) }
		.onChange(of: displayScale) { send(.didUpdateDisplayScale(displayScale)) }
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
	}

	private var preview: some View {
		CompactScoreSheets(
			games: Array(store.shareableGames.prefix(1)),
			configuration: store.configuration
		)
		.scrollDisabled(true)
		.cornerRadius(.standardRadius)
		.padding(.smallSpacing)
		.background(
			RoundedRectangle(cornerRadius: .standardRadius)
				.fill(store.style.cardBackground.swiftUIColor)
				.shadow(radius: .standardShadowRadius)
		)
		.padding(.standardSpacing)
	}

	@MainActor private var styleOptions: some View {
		Section(Strings.Sharing.Style.title) {
			Grid(horizontalSpacing: .smallSpacing, verticalSpacing: .smallSpacing) {
				ForEach(ScoreSheetStyleGroup.scoreSheetStyles) { group in
					GridRow {
						ForEach(group.styles) { style in
							Button { send(.didTapStyle(style)) } label: {
								PreviewingScoreSheet(style: style)
									.padding(.unitSpacing)
									.background(
										style.id == store.style.id
										? RoundedRectangle(cornerRadius: .standardRadius)
											.fill(Asset.Colors.List.selection.swiftUIColor)
										: nil
									)
							}
							.buttonStyle(TappableElement())
						}
					}
					.frame(maxWidth: .infinity)
				}
			}
		}
		.listRowBackground(Color.clear)
	}

	@MainActor private var frameOptions: some View {
		Section(Strings.Sharing.Frames.title) {
			Toggle(Strings.Sharing.Frames.includeDetails, isOn: $store.isShowingFrameDetails)
			Toggle(Strings.Sharing.Frames.includeLabels, isOn: $store.isShowingFrameLabels)
		}
	}

	@MainActor private var labelOptions: some View {
		Section {
			Toggle(Strings.Sharing.Labels.includeBowler, isOn: $store.isShowingBowlerName)
			Toggle(Strings.Sharing.Labels.includeLeague, isOn: $store.isShowingLeagueName)
			Toggle(Strings.Sharing.Labels.includeSeries, isOn: $store.isShowingSeriesDate)
			Toggle(Strings.Sharing.Labels.includeAlley, isOn: $store.isShowingAlleyName)
				.disabled(!store.hasAlley)
		} header: {
			Text(Strings.Sharing.Labels.title)
		} footer: {
			Text(Strings.Sharing.Labels.footer)
		}
	}

	@MainActor private var layoutOptions: some View {
		Section(Strings.Sharing.Layout.title) {
			Picker(
				Strings.Sharing.Layout.labelPosition,
				selection: $store.labelPosition
			) {
				ForEach(ScoreSheetConfiguration.LabelPosition.allCases) {
					Text(String(describing: $0)).tag($0)
				}
			}
		}
	}

	@MainActor private var shareButtons: some View {
		HStack {
//			Button {
//				viewStore.send(.didTapShareToStoriesButton)
//			} label: {
//				HStack {
//					Asset.Media.Icons.Social.instagram.swiftUIImage
//						.renderingMode(.template)
//						.resizable()
//						.scaledToFit()
//						.frame(width: .smallIcon)
//						.foregroundColor(.white)
//					Text(Strings.Action.shareToStories)
//				}
//				.frame(maxWidth: .infinity)
//			}
//			.modifier(PrimaryButton())

			//			ShareLi

			ShareLink(
				item: ShareableGameSet(
					games: store.shareableGames,
					configuration: store.configuration,
					scale: store.displayScale
				),
				preview: SharePreview("Image")
			) {
				Text(Strings.Action.shareToOther)
			}
			.buttonStyle(.borderedProminent)
			.controlSize(.large)
			.foregroundColor(Asset.Colors.Text.onSecondaryBackground)
			.tint(Asset.Colors.Background.secondary)
		}
		.padding()
	}
}

extension ScoreSheetConfiguration.Style: Identifiable {
	public var id: String { title }
}

struct ScoreSheetStyleGroup: Identifiable {
	let id = UUID()
	let styles: [ScoreSheetConfiguration.Style]

	static let scoreSheetStyles = ScoreSheetConfiguration.Style
		.allStyles
		.chunks(ofCount: 3)
		.map { ScoreSheetStyleGroup(styles: Array($0)) }
}

#if DEBUG
struct SharingViewPreview: PreviewProvider {
	static var previews: some View {
		NavigationStack {
			SharingView(store: .init(
				initialState: {
					var state = Sharing.State(dataSource: .games([UUID(0)]))
					state.games = .init(uniqueElements: [
						.init(
							id: UUID(0),
							index: 0,
							score: 123,
							scoringMethod: .byFrame,
							frames: [],
							bowler: .init(name: "Joseph"),
							league: .init(name: "Majors"),
							series: .init(
								date: Date(),
								alley: nil
//								alley: .init(name: "Skyview Lanes")
							)
						),
						.init(
							id: UUID(1),
							index: 1,
							score: 123,
							scoringMethod: .byFrame,
							frames: [],
							bowler: .init(name: "Joseph"),
							league: .init(name: "Majors"),
							series: .init(
								date: Date(),
								alley: nil
//								alley: .init(name: "Skyview Lanes")
							)
						),
					])

					state.scores = [
						UUID(0): .init(
							id: UUID(0),
							index: 0,
							frames: Game.FRAME_INDICES.map {
								.init(
									index: $0,
									rolls: [
										.init(index: 0, displayValue: "10", didFoul: true, isSecondary: false),
										.init(index: 1, displayValue: "HP", didFoul: false, isSecondary: false),
										.init(index: 2, displayValue: "3", didFoul: false, isSecondary: false),
									],
									score: 32
								)
							}
						),
						UUID(1): .init(
							id: UUID(1),
							index: 1,
							frames: Game.FRAME_INDICES.map {
								.init(
									index: $0,
									rolls: [
										.init(index: 0, displayValue: "10", didFoul: true, isSecondary: false),
										.init(index: 1, displayValue: "HP", didFoul: false, isSecondary: false),
										.init(index: 2, displayValue: "3", didFoul: false, isSecondary: false),
									],
									score: 32
								)
							}
						),
					]

					return state
				}(),
				reducer: Sharing.init
			))
		}
	}
}
#endif
