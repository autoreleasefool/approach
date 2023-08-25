import AssetsLibrary
import ComposableArchitecture
import EquatableLibrary
import ErrorsFeature
import FeatureActionLibrary
import ModelsLibrary
import ResourcePickerLibrary
import ScoreSheetFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary

public struct GamesEditorView: View {
	let store: StoreOf<GamesEditor>
	typealias GamesEditorViewStore = ViewStore<ViewState, GamesEditor.Action.ViewAction>

	@Environment(\.continuousClock) private var clock
	@Environment(\.safeAreaInsets) private var safeAreaInsets
	@State private var headerContentSize: CGSize = .zero
	@State private var frameContentSize: CGSize = .zero
	@State private var sheetContentSize: CGSize = .zero
	@State private var windowContentSize: CGSize = .zero
	@State private var minimumSheetContentSize: CGSize = .zero
	@State private var sectionHeaderContentSize: CGSize = .zero

	struct ViewState: Equatable {
		@BindingViewState var sheetDetent: PresentationDetent
		let willAdjustLaneLayoutAt: Date
		let backdropSize: CGSize

		let isScoreSheetVisible: Bool

		let manualScore: Int?

		let bowlerName: String?
		let leagueName: String?
	}

	public init(store: StoreOf<GamesEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			VStack {
				GamesHeaderView(store: store.scope(state: \.gamesHeader, action: /GamesEditor.Action.InternalAction.gamesHeader))
					.measure(key: HeaderContentSizeKey.self, to: $headerContentSize)

				VStack {
					if let manualScore = viewStore.manualScore {
						Spacer()

						VStack {
							Text(String(manualScore))
								.font(.largeTitle)
							Text(Strings.Game.Editor.Fields.ManualScore.caption)
								.font(.caption)
						}
						.padding()
						.background(.regularMaterial, in: RoundedRectangle(cornerRadius: .standardRadius, style: .continuous))
						.padding()

						Spacer()

					} else {

						Spacer()

						frameEditor
							.padding(.top)

						Spacer()

						rollEditor
							.padding(.horizontal)

						if viewStore.isScoreSheetVisible {
							scoreSheet
								.padding(.top)
								.padding(.horizontal)
								.measure(key: FrameContentSizeKey.self, to: $frameContentSize)
						}
					}
				}
				.frame(idealWidth: viewStore.backdropSize.width, maxHeight: viewStore.backdropSize.height)

				Spacer()
			}
			.measure(key: WindowContentSizeKey.self, to: $windowContentSize)
			.background(alignment: .top) {
				Asset.Media.Lane.backdrop.swiftUIImage
					.resizable(resizingMode: .stretch)
					.fixedSize(horizontal: true, vertical: false)
					.frame(width: viewStore.backdropSize.width, height: getBackdropHeight(viewStore))
					.padding(.top, headerContentSize.height)
			}
			.background(Color.black)
			.toolbar(.hidden, for: .tabBar, .navigationBar)
			.sheet(
				store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
				state: /GamesEditor.Destination.State.gameDetails,
				action: GamesEditor.Destination.Action.gameDetails
			) { (store: StoreOf<GameDetails>) in
				gameDetails(viewStore: viewStore, gameDetailsStore: store)
			}
			.onChange(of: viewStore.willAdjustLaneLayoutAt) { _ in
				viewStore.send(.didAdjustBackdropSize(getMeasuredBackdropSize(viewStore)), animation: .easeInOut)
			}
			.onFirstAppear {
				viewStore.send(.didFirstAppear)
				Task.detached {
					try await clock.sleep(for: .milliseconds(150))
					Task.detached { @MainActor in
						viewStore.send(.didAdjustBackdropSize(getMeasuredBackdropSize(viewStore)))
					}
				}
			}
		})
		.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /GamesEditor.Destination.State.ballPicker,
			action: GamesEditor.Destination.Action.ballPicker,
			onDismiss: { store.send(.internal(.didDismissOpenSheet)) },
			content: { (store: StoreOf<ResourcePicker<Gear.Summary, Bowler.ID>>) in
				ballPicker(store: store)
			}
		)
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /GamesEditor.Destination.State.opponentPicker,
			action: GamesEditor.Destination.Action.opponentPicker,
			onDismiss: { store.send(.internal(.didDismissOpenSheet)) },
			content: { (store: StoreOf<ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>>) in
				opponentPicker(store: store)
			}
		)
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /GamesEditor.Destination.State.gearPicker,
			action: GamesEditor.Destination.Action.gearPicker,
			onDismiss: { store.send(.internal(.didDismissOpenSheet)) },
			content: { (store: StoreOf<ResourcePicker<Gear.Summary, AlwaysEqual<Void>>>) in
				gearPicker(store: store)
			}
		)
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /GamesEditor.Destination.State.settings,
			action: GamesEditor.Destination.Action.settings,
			onDismiss: { store.send(.internal(.didDismissOpenSheet)) },
			content: { (store: StoreOf<GamesSettings>) in
				gamesSettings(store: store)
			}
		)
	}

	private func gameDetails(
		viewStore: GamesEditorViewStore,
		gameDetailsStore: StoreOf<GameDetails>
	) -> some View {
		Form {
			Section {
				IfLetStore(store.scope(state: \.gameDetailsHeader, action: /GamesEditor.Action.InternalAction.gameDetailsHeader)) {
					GameDetailsHeaderView(store: $0)
						.measure(key: MinimumSheetContentSizeKey.self, to: $minimumSheetContentSize)
				}
			} header: {
				Color.clear
					.measure(key: SectionHeaderContentSizeKey.self, to: $sectionHeaderContentSize)
			}

			GameDetailsView(store: gameDetailsStore)
		}
		.padding(.top, -sectionHeaderContentSize.height)
		.frame(minHeight: 50)
		.edgesIgnoringSafeArea(.bottom)
		.presentationDetents(
			[
				.height(minimumSheetContentSize.height + 40),
				.medium,
				.large,
			],
			selection: viewStore.$sheetDetent
		)
		.presentationBackgroundInteraction(.enabled(upThrough: .medium))
		.interactiveDismissDisabled(true)
		.measure(key: SheetContentSizeKey.self, to: $sheetContentSize)
	}

	private func ballPicker(store: StoreOf<ResourcePicker<Gear.Summary, Bowler.ID>>) -> some View {
		NavigationStack {
			ResourcePickerView(store: store) {
				Gear.View(gear: $0)
			}
		}
	}

	private func opponentPicker(store: StoreOf<ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>>) -> some View {
		NavigationStack {
			ResourcePickerView(store: store) {
				Text($0.name)
			}
		}
	}

	private func gearPicker(store: StoreOf<ResourcePicker<Gear.Summary, AlwaysEqual<Void>>>) -> some View {
		NavigationStack {
			ResourcePickerView(store: store) {
				Gear.View(gear: $0)
			}
		}
	}

	private func gamesSettings(store: StoreOf<GamesSettings>) -> some View {
		NavigationStack {
			GamesSettingsView(store: store)
		}
	}

	private var frameEditor: some View {
		IfLetStore(store.scope(state: \.frameEditor, action: /GamesEditor.Action.InternalAction.frameEditor)) {
			FrameEditorView(store: $0)
		}
	}

	private var rollEditor: some View {
		IfLetStore(store.scope(state: \.rollEditor, action: /GamesEditor.Action.InternalAction.rollEditor)) {
			RollEditorView(store: $0)
		}
	}

	private var scoreSheet: some View {
		IfLetStore(store.scope(state: \.scoreSheet, action: /GamesEditor.Action.InternalAction.scoreSheet)) {
			ScoreSheetView(store: $0)
		}
	}

	private func getMeasuredBackdropSize(_ viewStore: GamesEditorViewStore) -> CGSize {
		let sheetContentSize = viewStore.sheetDetent == .large ? .zero : self.sheetContentSize
		return .init(
			width: windowContentSize.width,
			height: windowContentSize.height - sheetContentSize.height - headerContentSize.height
				- safeAreaInsets.bottom - CGFloat.largeSpacing
		)
	}

	private func getBackdropHeight(_ viewStore: GamesEditorViewStore) -> CGFloat {
		max(viewStore.backdropSize.height - (viewStore.isScoreSheetVisible ? frameContentSize.height : 0), 0)
	}
}

extension GamesEditorView.ViewState {
	init(store: BindingViewStore<GamesEditor.State>) {
		self._sheetDetent = store.$sheetDetent
		self.willAdjustLaneLayoutAt = store.willAdjustLaneLayoutAt
		self.backdropSize = store.backdropSize
		self.isScoreSheetVisible = store.isScoreSheetVisible
		self.bowlerName = store.game?.bowler.name
		self.leagueName = store.game?.league.name
		if let game = store.game {
			switch game.scoringMethod {
			case .byFrame:
				self.manualScore = nil
			case .manual:
				self.manualScore = game.score
			}
		} else {
			self.manualScore = nil
		}
	}
}

private struct MinimumSheetContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct SheetContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct WindowContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct HeaderContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct FrameContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct SectionHeaderContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
