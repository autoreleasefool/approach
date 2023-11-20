import Algorithms
import AssetsLibrary
import ComposableArchitecture
import Foundation
import ModelsLibrary
import ScoreSheetLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct SharingView: View {
	let store: StoreOf<Sharing>

	@Environment(\.displayScale) var displayScale

	public init(store: StoreOf<Sharing>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			VStack(spacing: 0) {
				preview(viewStore)

				List {
					styleOptions(viewStore)
					frameOptions(viewStore)
					labelOptions(viewStore)
					layoutOptions(viewStore)
				}

				Divider()

				shareButtons(viewStore)
			}
			.navigationTitle(viewStore.navigationTitle)
			.navigationBarTitleDisplayMode(.inline)
			.toolbar {
				ToolbarItem(placement: .navigationBarLeading) {
					Button(Strings.Action.done) { viewStore.send(.didTapDoneButton) }
				}
			}
			.onFirstAppear { viewStore.send(.didFirstAppear) }
			.onFirstAppear {
				viewStore.send(.binding(.set(\.$displayScale, displayScale)))
			}
			.onAppear { viewStore.send(.onAppear) }
			.onChange(of: displayScale) {
				viewStore.send(.binding(.set(\.$displayScale, $0)))
			}
		})
		.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
	}

	@MainActor private func preview(
		_ viewStore: ViewStore<Sharing.State, Sharing.Action.ViewAction>
	) -> some View {
		CompactScoreSheets(
			games: Array(viewStore.shareableGames.prefix(1)),
			configuration: viewStore.configuration
		)
		.scrollDisabled(true)
		.cornerRadius(.standardRadius)
		.padding(.smallSpacing)
		.background(
			RoundedRectangle(cornerRadius: .standardRadius)
				.fill(viewStore.style.cardBackground.swiftUIColor)
				.shadow(radius: .standardShadowRadius)
		)
		.padding(.standardSpacing)
	}

	@MainActor private func styleOptions(
		_ viewStore: ViewStore<Sharing.State, Sharing.Action.ViewAction>
	) -> some View {
		Section(Strings.Sharing.Style.title) {
			Grid(horizontalSpacing: .smallSpacing, verticalSpacing: .smallSpacing) {
				ForEach(ScoreSheetStyleGroup.scoreSheetStyles) { group in
					GridRow {
						ForEach(group.styles) { style in
							Button { viewStore.send(.didTapStyle(style)) } label: {
								PreviewingScoreSheet(style: style)
									.padding(.unitSpacing)
									.background(
										style.id == viewStore.style.id
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

	@MainActor private func frameOptions(_ viewStore: ViewStore<Sharing.State, Sharing.Action.ViewAction>) -> some View {
		Section(Strings.Sharing.Frames.title) {
			Toggle(Strings.Sharing.Frames.includeDetails, isOn: viewStore.$isShowingFrameDetails)
			Toggle(Strings.Sharing.Frames.includeLabels, isOn: viewStore.$isShowingFrameLabels)
		}
	}

	@MainActor private func labelOptions(_ viewStore: ViewStore<Sharing.State, Sharing.Action.ViewAction>) -> some View {
		Section {
			Toggle(Strings.Sharing.Labels.includeBowler, isOn: viewStore.$isShowingBowlerName)
			Toggle(Strings.Sharing.Labels.includeLeague, isOn: viewStore.$isShowingLeagueName)
			Toggle(Strings.Sharing.Labels.includeSeries, isOn: viewStore.$isShowingSeriesDate)
			Toggle(Strings.Sharing.Labels.includeAlley, isOn: viewStore.$isShowingAlleyName)
				.disabled(!viewStore.hasAlley)
		} header: {
			Text(Strings.Sharing.Labels.title)
		} footer: {
			Text(Strings.Sharing.Labels.footer)
		}
	}

	@MainActor private func layoutOptions(
		_ viewStore: ViewStore<Sharing.State, Sharing.Action.ViewAction>
	) -> some View {
		Section(Strings.Sharing.Layout.title) {
			Picker(
				Strings.Sharing.Layout.labelPosition,
				selection: viewStore.$labelPosition
			) {
				ForEach(ScoreSheetConfiguration.LabelPosition.allCases) {
					Text(String(describing: $0)).tag($0)
				}
			}
		}
	}

	@MainActor private func shareButtons(
		_ viewStore: ViewStore<Sharing.State, Sharing.Action.ViewAction>
	) -> some View {
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
					games: viewStore.shareableGames,
					configuration: viewStore.configuration,
					scale: viewStore.displayScale
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
										.init(index: 0, displayValue: "10", didFoul: true),
										.init(index: 1, displayValue: "HP", didFoul: false),
										.init(index: 2, displayValue: "3", didFoul: false),
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
										.init(index: 0, displayValue: "10", didFoul: true),
										.init(index: 1, displayValue: "HP", didFoul: false),
										.init(index: 2, displayValue: "3", didFoul: false),
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
