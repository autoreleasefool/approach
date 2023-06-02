import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import ResourcePickerLibrary
import ScoreSheetFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary

public struct GamesEditorView: View {
	let store: StoreOf<GamesEditor>

	@Environment(\.continuousClock) private var clock
	@Environment(\.safeAreaInsets) private var safeAreaInsets
	@State private var headerContentSize: CGSize = .zero
	@State private var frameContentSize: CGSize = .zero
	@State private var sheetContentSize: CGSize = .zero
	@State private var windowContentSize: CGSize = .zero
	@State private var minimumSheetContentSize: CGSize = .zero
	@State private var sectionHeaderContentSize: CGSize = .zero

	struct ViewState: Equatable {
		let sheetDetent: PresentationDetent
		let willAdjustLaneLayoutAt: Date
		let backdropSize: CGSize

		let isGameDetailsPresented: Bool
		let isBallPickerPresented: Bool
		let isOpponentPickerPresented: Bool
		let isGearPickerPresented: Bool
		let isSettingsPresented: Bool

		let isScoreSheetVisible: Bool

		let manualScore: Int?

		let bowlerName: String?
		let leagueName: String?

		init(state: GamesEditor.State) {
			self.sheetDetent = state.sheetDetent
			self.willAdjustLaneLayoutAt = state.willAdjustLaneLayoutAt
			self.backdropSize = state.backdropSize
			self.isGameDetailsPresented = state.sheet == .presenting(.gameDetails)
			self.isBallPickerPresented = state.sheet == .presenting(.ballPicker)
			self.isOpponentPickerPresented = state.sheet == .presenting(.opponentPicker)
			self.isSettingsPresented = state.sheet == .presenting(.settings)
			self.isGearPickerPresented = state.sheet == .presenting(.gearPicker)
			self.isScoreSheetVisible = state.isScoreSheetVisible
			self.bowlerName = state.game?.bowler.name
			self.leagueName = state.game?.league.name
			if let game = state.game {
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

	enum ViewAction {
		case didAppear
		case didChangeDetent(PresentationDetent)
		case didAdjustBackdropSize(CGSize)

		case setGameDetails(isPresented: Bool)
		case setBallPicker(isPresented: Bool)
		case setOpponentPicker(isPresented: Bool)
		case setGearPicker(isPresented: Bool)
		case setSettings(isPresented: Bool)

		case didDismissGameDetails
		case didDismissBallPicker
		case didDismissOpponentPicker
		case didDismissGearPicker
		case didDismissGamesSettings
	}

	public init(store: StoreOf<GamesEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GamesEditor.Action.init) { viewStore in
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
				Image(uiImage: .laneBackdrop)
					.resizable(resizingMode: .stretch)
					.fixedSize(horizontal: true, vertical: false)
					.frame(width: viewStore.backdropSize.width, height: getBackdropHeight(viewStore))
					.padding(.top, headerContentSize.height)
			}
			.background(Color.black)
			.toolbar(.hidden, for: .tabBar, .navigationBar)
			.sheet(
				isPresented: viewStore.binding(get: \.isBallPickerPresented, send: ViewAction.setBallPicker(isPresented:)),
				onDismiss: { viewStore.send(.didDismissBallPicker) },
				content: {
					ballPicker
						.presentationDetents([.medium])
				}
			)
			.sheet(
				isPresented: viewStore.binding(get: \.isOpponentPickerPresented, send: ViewAction.setOpponentPicker(isPresented:)),
				onDismiss: { viewStore.send(.didDismissOpponentPicker) },
				content: {
					opponentPicker
						.presentationDetents([.large])
				}
			)
			.sheet(
				isPresented: viewStore.binding(get: \.isGearPickerPresented, send: ViewAction.setGearPicker(isPresented:)),
				onDismiss: { viewStore.send(.didDismissGearPicker) },
				content: {
					gearPicker
						.presentationDetents([.large])
				}
			)
			.sheet(
				isPresented: viewStore.binding(get: \.isSettingsPresented, send: ViewAction.setSettings(isPresented:)),
				onDismiss: { viewStore.send(.didDismissGamesSettings) },
				content: {
					gamesSettings
						.presentationDetents([.large])
				}
			)
			.sheet(
				isPresented: viewStore.binding(get: \.isGameDetailsPresented, send: ViewAction.setGameDetails(isPresented:)),
				onDismiss: { viewStore.send(.didDismissGameDetails) },
				content: {
					gameDetails
						.padding(.top, -sectionHeaderContentSize.height)
						.frame(minHeight: 50)
						.edgesIgnoringSafeArea(.bottom)
						.presentationDetents(
							[
								.height(minimumSheetContentSize.height + 40),
								.medium,
								.large,
							],
							selection: viewStore.binding(get: \.sheetDetent, send: ViewAction.didChangeDetent)
						)
						.presentationBackgroundInteraction(.enabled(upThrough: .medium))
						.interactiveDismissDisabled(true)
						.measure(key: SheetContentSizeKey.self, to: $sheetContentSize)
				}
			)
			.onChange(of: viewStore.willAdjustLaneLayoutAt) { _ in
				viewStore.send(.didAdjustBackdropSize(getMeasuredBackdropSize(viewStore)), animation: .easeInOut)
			}
			.onAppear {
				viewStore.send(.didAppear)
				Task.detached {
					try await clock.sleep(for: .milliseconds(150))
					Task.detached { @MainActor in
						viewStore.send(.didAdjustBackdropSize(getMeasuredBackdropSize(viewStore)))
					}
				}
			}
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

	private var ballPicker: some View {
		NavigationView {
			BallPickerView(
				store: store.scope(state: \.ballPicker, action: /GamesEditor.Action.InternalAction.ballPicker)
			)
		}
	}

	private var opponentPicker: some View {
		NavigationView {
			ResourcePickerView(
				store: store.scope(state: \.opponentPicker, action: /GamesEditor.Action.InternalAction.opponentPicker)
			) {
				Text($0.name)
			}
		}
	}

	private var gearPicker: some View {
		NavigationView {
			ResourcePickerView(
				store: store.scope(state: \.gearPicker, action: /GamesEditor.Action.InternalAction.gearPicker)
			) {
				Text($0.name)
			}
		}
	}

	private var gamesSettings: some View {
		NavigationView {
			IfLetStore(store.scope(state: \.gamesSettings, action: /GamesEditor.Action.InternalAction.gamesSettings)) {
				GamesSettingsView(store: $0)
			}
		}
	}

	private var gameDetails: some View {
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

			IfLetStore(store.scope(state: \.gameDetails, action: /GamesEditor.Action.InternalAction.gameDetails)) {
				GameDetailsView(store: $0)
			}
		}
	}

	private func getMeasuredBackdropSize(_ viewStore: ViewStore<ViewState, ViewAction>) -> CGSize {
		let sheetContentSize = viewStore.sheetDetent == .large ? .zero : self.sheetContentSize
		return .init(
			width: windowContentSize.width,
			height: windowContentSize.height - sheetContentSize.height - headerContentSize.height
				- safeAreaInsets.bottom - CGFloat.largeSpacing
		)
	}

	private func getBackdropHeight(_ viewStore: ViewStore<ViewState, ViewAction>) -> CGFloat {
		max(viewStore.backdropSize.height - (viewStore.isScoreSheetVisible ? frameContentSize.height : 0), 0)
	}
}

extension GamesEditor.Action {
	init(action: GamesEditorView.ViewAction) {
		switch action {
		case .didAppear:
			self = .view(.didAppear)
		case let .setGameDetails(isPresented):
			self = .view(.setGameDetails(isPresented: isPresented))
		case let .setBallPicker(isPresented):
			self = .view(.setBallPicker(isPresented: isPresented))
		case let .setOpponentPicker(isPresented):
			self = .view(.setOpponentPicker(isPresented: isPresented))
		case let .setGearPicker(isPresented):
			self = .view(.setGearPicker(isPresented: isPresented))
		case let .setSettings(isPresented):
			self = .view(.setGamesSettings(isPresented: isPresented))
		case .didDismissGameDetails:
			self = .view(.didDismissOpenSheet)
		case .didDismissBallPicker:
			self = .view(.didDismissOpenSheet)
		case .didDismissOpponentPicker:
			self = .view(.didDismissOpenSheet)
		case .didDismissGamesSettings:
			self = .view(.didDismissOpenSheet)
		case .didDismissGearPicker:
			self = .view(.didDismissOpenSheet)
		case let .didChangeDetent(newDetent):
			self = .view(.didChangeDetent(newDetent))
		case let .didAdjustBackdropSize(newSize):
			self = .view(.didAdjustBackdropSize(newSize))
		}
	}
}

private struct MinimumSheetContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct SheetContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct WindowContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct HeaderContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct FrameContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct SectionHeaderContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
