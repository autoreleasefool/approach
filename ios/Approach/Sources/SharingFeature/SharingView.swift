import Algorithms
import AssetsLibrary
import ComposableArchitecture
import Foundation
import ModelsLibrary
import ScoreSheetFeature
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct SharingView: View {
	let store: StoreOf<Sharing>

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

				Button {
					viewStore.send(.didTapShareButton)
				} label: {
					Text(Strings.Action.share)
						.frame(maxWidth: .infinity)
				}
				.modifier(PrimaryButton())
				.padding()
			}
			.navigationTitle(viewStore.navigationTitle)
			.navigationBarTitleDisplayMode(.inline)
			.toolbar {
				ToolbarItem(placement: .navigationBarLeading) {
					Button(Strings.Action.done) { viewStore.send(.didTapDoneButton) }
				}
			}
//			.onFirstAppear { viewStore.send(.didFirstAppear) }
		})
		.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
	}

	@MainActor private func preview(
		_ viewStore: ViewStore<Sharing.State, Sharing.Action.ViewAction>
	) -> some View {
		ShareableScoreSheetView(
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
				ForEach(ShareableScoreSheetStyleGroup.scoreSheetStyles) { group in
					GridRow {
						ForEach(group.styles) { style in
							Button { viewStore.send(.didTapStyle(style)) } label: {
								PreviewingShareableScoreSheetView(style: style)
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
				ForEach(ShareableScoreSheetConfiguration.LabelPosition.allCases) {
					Text(String(describing: $0)).tag($0)
				}
			}
		}
	}
}

extension ShareableScoreSheetConfiguration.Style: Identifiable {
	public var id: String { title }
}

struct ShareableScoreSheetStyleGroup: Identifiable {
	let id = UUID()
	let styles: [ShareableScoreSheetConfiguration.Style]

	static let scoreSheetStyles = ShareableScoreSheetConfiguration.Style
		.allStyles
		.chunks(ofCount: 3)
		.map { ShareableScoreSheetStyleGroup(styles: Array($0)) }
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
						UUID(0): Game.FRAME_INDICES.map {
							.init(
								index: $0,
								rolls: [
									.init(index: 0, display: "10", didFoul: true),
									.init(index: 1, display: "HP", didFoul: false),
									.init(index: 2, display: "3", didFoul: false),
								],
								score: 32
							)
						},
						UUID(1): Game.FRAME_INDICES.map {
							.init(
								index: $0,
								rolls: [
									.init(index: 0, display: "10", didFoul: true),
									.init(index: 1, display: "HP", didFoul: false),
									.init(index: 2, display: "3", didFoul: false),
								],
								score: 32
							)
						},
					]

					return state
				}(),
				reducer: Sharing.init
			))
		}
	}
}
#endif
