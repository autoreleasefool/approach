import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import StatisticsLibrary
import StringsLibrary
import SwiftUI

@Reducer
public struct Sharing: Reducer {
	@ObservableState
	public struct State: Equatable {
		public let source: Source

		public var seriesSharing: SeriesSharing.State?
		public var statisticsSharing: StatisticsWidgetSharing.State?
		public var gamesSharing: GamesSharing.State?

		public var tabs: [SharingTab]
		public var selectedTab: SharingTab

		public var lastPreviewPerTab: [SharingTab: Preview] = [:]
		public var isPreviewingImage: Bool = false
		public var preview: Preview?

		public init(source: Source) {
			self.source = source

			switch source {
			case let .series(seriesId):
				seriesSharing = .init(seriesId: seriesId)
				gamesSharing = .init(seriesId: seriesId)
				tabs = [.series, .games]
				selectedTab = .series
			case let .statistic(source, statistic):
				statisticsSharing = .init(source: source, statistic: statistic)
				tabs = [.statistic]
				selectedTab = .statistic
			case let .games(seriesId):
				seriesSharing = .init(seriesId: seriesId)
				gamesSharing = .init(seriesId: seriesId)
				tabs = [.series, .games]
				selectedTab = .games
			}
		}
	}

	public enum Action: BindableAction, FeatureAction, ViewAction {
		@CasePathable public enum View {
			case onAppear
			case didTapSaveButton
			case didTapShareButton
			case didTapDoneButton
			case didTapPreviewImage
			case didTapBackdrop
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case seriesSharing(SeriesSharing.Action)
			case statisticsSharing(StatisticsWidgetSharing.Action)
			case gamesSharing(GamesSharing.Action)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public enum Source: Equatable {
		case games(Series.ID)
		case series(Series.ID)
		case statistic(StatisticsWidget.Source?, statistic: String?)
	}

	public enum SharingTab: Identifiable, Hashable, CustomStringConvertible {
		case series
		case games
		case statistic

		public var id: Self { self }

		public var description: String {
			switch self {
			case .games: Strings.Sharing.Tabs.games
			case .series: Strings.Sharing.Tabs.series
			case .statistic: Strings.Sharing.Tabs.statistic
			}
		}
	}

	public struct Preview: Equatable {
		public let preview: UIImage
		public let image: Image
	}

	public init() {}

	@Dependency(\.dismiss) var dismiss

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didTapSaveButton:
					return .none

				case .didTapShareButton:
					return .none

				case .didTapDoneButton:
					return .run { _ in await dismiss() }

				case .didTapPreviewImage:
					state.isPreviewingImage = true
					return .none

				case .didTapBackdrop:
					state.isPreviewingImage = false
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .seriesSharing(.delegate(.imageRendered(image))):
					return updatePreview(image, forTab: .series, state: &state)

				case let .statisticsSharing(.delegate(.imageRendered(image))):
					return updatePreview(image, forTab: .statistic, state: &state)

				case let .gamesSharing(.delegate(.imageRendered(image))):
					return updatePreview(image, forTab: .games, state: &state)

				case .statisticsSharing(.binding), .statisticsSharing(.internal), .statisticsSharing(.view),
						.seriesSharing(.binding), .seriesSharing(.internal), .seriesSharing(.view),
						.gamesSharing(.binding), .gamesSharing(.internal), .gamesSharing(.view):
					return .none
				}

			case .binding(\.selectedTab):
				state.preview = state.lastPreviewPerTab[state.selectedTab]
				return .none

			case .delegate, .binding:
				return .none
			}
		}
		.ifLet(\.seriesSharing, action: \.internal.seriesSharing) {
			SeriesSharing()
		}
		.ifLet(\.statisticsSharing, action: \.internal.statisticsSharing) {
			StatisticsWidgetSharing()
		}
		.ifLet(\.gamesSharing, action: \.internal.gamesSharing) {
			GamesSharing()
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}
	}

	private func updatePreview(_ image: UIImage, forTab: SharingTab, state: inout State) -> Effect<Action> {
		let preview = Preview(preview: image, image: Image(uiImage: image))
		state.lastPreviewPerTab[forTab] = preview
		state.preview = preview
		return .none
	}
}

// MARK: - View

@ViewAction(for: Sharing.self)
public struct SharingView: View {
	@Namespace private var previewNamespace

	@Bindable public var store: StoreOf<Sharing>

	public init(store: StoreOf<Sharing>) {
		self.store = store
	}

	public var body: some View {
		ZStack {
			VStack(spacing: 0) {
				if store.tabs.count > 1 {
					tabs
				} else {
					switch store.selectedTab {
					case .series:
						seriesSharing
					case .statistic:
						statisticsSharing
					case .games:
						gamesSharing
					}
				}

				VStack(spacing: 0) {
					Divider()

					if !store.isPreviewingImage {
						previewImage
							.matchedGeometryEffect(id: "Preview", in: previewNamespace)
							.padding(.top)
							.padding(.horizontal)
					}

					shareButton
						.padding()
				}
			}
			.blur(radius: store.isPreviewingImage ? .largeRadius : 0)
			.disabled(store.isPreviewingImage)
			.zIndex(1)

			if let preview = store.preview?.preview, store.isPreviewingImage {
				Color.clear
					.onTapGesture { send(.didTapBackdrop, animation: .easeInOut) }
					.zIndex(2)

				ModalImagePreview(image: preview, namespace: previewNamespace) {
					send(.didTapBackdrop, animation: .easeInOut)
				}
				.zIndex(3)
			}
		}
		.navigationTitle(Strings.Sharing.title)
		.navigationBarTitleDisplayMode(.inline)
		.toolbar {
			ToolbarItem(placement: .navigationBarLeading) {
				Button(Strings.Action.done) { send(.didTapDoneButton) }
			}
		}
		.onAppear { send(.onAppear) }
	}

	private var tabs: some View {
		VStack(spacing: 0) {
			Picker(
				Strings.Sharing.Tabs.title,
				selection: $store.selectedTab.animation()
			) {
				ForEach(store.tabs) {
					Text(String(describing: $0)).tag($0)
				}
			}
			.pickerStyle(.segmented)
			.padding(.horizontal)
			.padding(.bottom)

			TabView(selection: $store.selectedTab) {
				seriesSharing
					.tag(Sharing.SharingTab.series)

				gamesSharing
					.tag(Sharing.SharingTab.games)

				statisticsSharing
					.tag(Sharing.SharingTab.statistic)
			}
			.tabViewStyle(.page(indexDisplayMode: .never))
		}
	}

	@ViewBuilder
	private var seriesSharing: some View {
		if let store = store.scope(state: \.seriesSharing, action: \.internal.seriesSharing) {
			SeriesSharingView(store: store)
		}
	}

	@ViewBuilder
	private var statisticsSharing: some View {
		if let store = store.scope(state: \.statisticsSharing, action: \.internal.statisticsSharing) {
			StatisticsWidgetSharingView(store: store)
		}
	}

	@ViewBuilder
	private var gamesSharing: some View {
		if let store = store.scope(state: \.gamesSharing, action: \.internal.gamesSharing) {
			GamesSharingView(store: store)
		}
	}

	@ViewBuilder
	private var previewImage: some View {
		if let preview = store.preview?.image {
			preview
				.resizable()
				.frame(maxWidth: .infinity)
				.aspectRatio(contentMode: .fit)
				.clipShape(RoundedRectangle(cornerRadius: .standardRadius))
				.onTapGesture { send(.didTapPreviewImage, animation: .easeInOut) }
		}
	}

	@ViewBuilder
	private var shareButton: some View {
		if let image = store.preview?.image {
			ShareLink(item: image, preview: SharePreview(Strings.App.name, image: image)) {
				ShareImageButton()
			}
			.modifier(ShareImageButtonModifier())
		} else {
			Button { } label: {
				ShareImageButton()
			}
			.modifier(ShareImageButtonModifier())
			.disabled(true)
		}
	}
}

private struct ShareImageButton: View {
	var body: some View {
		HStack(alignment: .center, spacing: .standardSpacing) {
			Spacer()

			Image(systemSymbol: .squareAndArrowUp)
				.resizable()
				.scaledToFit()
				.frame(width: .smallIcon, height: .smallIcon)

			Text(Strings.Action.share)
				.font(.subheadline)
				.fontWeight(.bold)

			Spacer()
		}
	}
}

private struct ShareImageButtonModifier: ViewModifier {
	func body(content: Content) -> some View {
		content
			.buttonStyle(.borderedProminent)
			.controlSize(.large)
			.foregroundColor(Asset.Colors.Text.onPrimary)
			.tint(Asset.Colors.Primary.default)
	}
}

