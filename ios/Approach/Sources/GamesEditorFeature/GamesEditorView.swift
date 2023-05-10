import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
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

		let isGamePickerPresented: Bool
		let isGameDetailsPresented: Bool
		let isBallPickerPresented: Bool
		let isSettingsPresented: Bool

		let isScoreSheetVisible: Bool

		let bowlerName: String?
		let leagueName: String?

		init(state: GamesEditor.State) {
			self.sheetDetent = state.sheetDetent
			self.willAdjustLaneLayoutAt = state.willAdjustLaneLayoutAt
			self.backdropSize = state.backdropSize
			self.isGameDetailsPresented = state.sheet == .presenting(.gameDetails)
			self.isGamePickerPresented = state.sheet == .presenting(.gamePicker)
			self.isBallPickerPresented = state.sheet == .presenting(.ballPicker)
			self.isSettingsPresented = state.sheet == .presenting(.settings)
			self.isScoreSheetVisible = state.isScoreSheetVisible
			self.bowlerName = state.currentGame?.bowler.name
			self.leagueName = state.currentGame?.league.name
		}
	}

	enum ViewAction {
		case didAppear
		case didChangeDetent(PresentationDetent)
		case didAdjustBackdropSize(CGSize)

		case setGamePicker(isPresented: Bool)
		case setGameDetails(isPresented: Bool)
		case setBallPicker(isPresented: Bool)
		case setSettings(isPresented: Bool)

		case didDismissGameDetails
		case didDismissGamePicker
		case didDismissBallPicker
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
				isPresented: viewStore.binding(get: \.isGamePickerPresented, send: ViewAction.setGamePicker(isPresented:)),
				onDismiss: { viewStore.send(.didDismissGamePicker) },
				content: {
					gamePicker
						.presentationDetents([.medium])
				}
			)
			.sheet(
				isPresented: viewStore.binding(get: \.isBallPickerPresented, send: ViewAction.setBallPicker(isPresented:)),
				onDismiss: { viewStore.send(.didDismissBallPicker) },
				content: {
					ballPicker
						.presentationDetents([.medium])
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

	private var gamePicker: some View {
		NavigationView {
			GamePickerView(store: store.scope(state: \.gamePicker, action: /GamesEditor.Action.InternalAction.gamePicker))
		}
	}

	private var ballPicker: some View {
		NavigationView {
			BallPickerView(
				store: store.scope(state: \.ballPicker, action: /GamesEditor.Action.InternalAction.ballPicker)
			)
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
		case let .setGamePicker(isPresented):
			self = .view(.setGamePicker(isPresented: isPresented))
		case let .setBallPicker(isPresented):
			self = .view(.setBallPicker(isPresented: isPresented))
		case let .setSettings(isPresented):
			self = .view(.setGamesSettings(isPresented: isPresented))
		case .didDismissGamePicker:
			self = .view(.didDismissOpenSheet)
		case .didDismissGameDetails:
			self = .view(.didDismissOpenSheet)
		case .didDismissBallPicker:
			self = .view(.didDismissOpenSheet)
		case .didDismissGamesSettings:
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
