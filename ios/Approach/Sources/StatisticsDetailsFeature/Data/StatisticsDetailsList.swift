import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import PreferenceServiceInterface
import StatisticsLibrary
import StatisticsRepositoryInterface
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import TipsLibrary
import TipsServiceInterface
import ViewsLibrary

@Reducer
public struct StatisticsDetailsList: Reducer {
	public struct State: Equatable {
		@BindingState public var isHidingZeroStatistics: Bool
		@BindingState public var isHidingStatisticsDescriptions: Bool

		public var listEntries: IdentifiedArrayOf<Statistics.ListEntryGroup> = []
		public var entryToHighlight: Statistics.ListEntry.ID?
		public let hasTappableElements: Bool

		public var isShowingStatisticDescriptionTip: Bool

		init(listEntries: IdentifiedArrayOf<Statistics.ListEntryGroup>, hasTappableElements: Bool) {
			self.listEntries = listEntries
			self.hasTappableElements = hasTappableElements

			@Dependency(\.preferences) var preferences
			self.isHidingZeroStatistics = preferences.bool(forKey: .statisticsHideZeroStatistics) ?? true
			self.isHidingStatisticsDescriptions = preferences.bool(forKey: .statisticsHideStatisticsDescriptions) ?? false

			@Dependency(\.tips) var tips
			self.isShowingStatisticDescriptionTip = tips.shouldShow(tipFor: .statisticsDescriptionTip)
		}
	}

	public enum Action: FeatureAction {
		@CasePathable public enum ViewAction: BindableAction {
			case didTapEntry(id: String)
			case didTapDismissDescriptionsTip
			case binding(BindingAction<State>)
		}
		@CasePathable public enum DelegateAction {
			case didRequestEntryDetails(id: String)
			case listRequiresReload
		}
		@CasePathable public enum InternalAction {
			case scrollToEntry(id: Statistics.ListEntry.ID?)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	enum CancelID {
		case setHidingZeroStatistics
		case setHidingStatisticsDescriptions
	}

	public init() {}

	@Dependency(\.preferences) var preferences
	@Dependency(\.tips) var tips

	public var body: some ReducerOf<Self> {
		BindingReducer(action: \.view)

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapEntry(id):
					return .send(.delegate(.didRequestEntryDetails(id: id)))

				case .didTapDismissDescriptionsTip:
					state.isShowingStatisticDescriptionTip = false
					return .run { _ in await tips.hide(tipFor: .statisticsDescriptionTip) }

				case .binding(\.$isHidingZeroStatistics):
					return .concatenate(
						.run { [updatedValue = state.isHidingZeroStatistics] _ in
							preferences.setKey(.statisticsHideZeroStatistics, toBool: updatedValue)
						},
						.send(.delegate(.listRequiresReload))
					)
					.cancellable(id: CancelID.setHidingZeroStatistics, cancelInFlight: true)

				case .binding(\.$isHidingStatisticsDescriptions):
					return .concatenate(
						.run { [updatedValue = state.isHidingStatisticsDescriptions] _ in
							preferences.setKey(.statisticsHideStatisticsDescriptions, toBool: updatedValue)
						},
						.send(.delegate(.listRequiresReload))
					)
					.cancellable(id: CancelID.setHidingStatisticsDescriptions, cancelInFlight: true)

				case .binding:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .scrollToEntry(id):
					state.entryToHighlight = id
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

// MARK: - View

public struct StatisticsDetailsListView<Header: View>: View {
	let store: StoreOf<StatisticsDetailsList>
	let header: Header

	init(store: StoreOf<StatisticsDetailsList>, @ViewBuilder header: () -> Header) {
		self.store = store
		self.header = header()
	}

	init(store: StoreOf<StatisticsDetailsList>) where Header == EmptyView {
		self.init(store: store, header: { EmptyView() })
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			ScrollViewReader { scrollViewProxy in
				List {
					header

					if viewStore.isShowingStatisticDescriptionTip {
						BasicTipView(tip: .statisticsDescriptionTip) {
							viewStore.send(.didTapDismissDescriptionsTip, animation: .default)
						}
					}

					ForEach(viewStore.listEntries) { group in
						Section(group.title) {
							if group.description != nil || group.images != nil {
								VStack(alignment: .center) {
									if let description = group.description {
										Text(description)
											.font(.caption)
									}

									if let images = group.images, !images.isEmpty {
										HStack(alignment: .center) {
											ForEach(images) {
												Image(uiImage: $0.image)
													.resizable()
													.scaledToFit()
													.frame(width: .smallerIcon, height: .smallerIcon)
											}
										}
									}
								}
							}

							ForEach(group.entries) { entry in
								Button { viewStore.send(.didTapEntry(id: entry.id)) } label: {
									HStack(alignment: .center, spacing: .smallSpacing) {
										if entry.highlightAsNew {
											Text(Strings.Statistics.List.new.uppercased())
												.font(.caption)
												.fontWeight(.thin)
												.foregroundColor(Asset.Colors.Action.default)
										}

										VStack(alignment: .leading) {
											Text(entry.title)
											if !viewStore.isHidingStatisticsDescriptions, let description = entry.description {
												Text(description)
													.font(.caption2)
											}
										}

										Spacer()

										VStack(alignment: .trailing) {
											Text(entry.value)
											if !viewStore.isHidingStatisticsDescriptions, let valueDescription = entry.valueDescription {
												Text(valueDescription)
													.font(.caption2)
											}
										}
									}
								}
								.if(!viewStore.hasTappableElements) {
									$0.buttonStyle(.plain)
								}
								.if(viewStore.hasTappableElements) {
									$0
										.buttonStyle(.navigation)
										.contentShape(Rectangle())
								}
								.listRowBackground(
									entry.id == viewStore.entryToHighlight ? Asset.Colors.Charts.List.background.swiftUIColor : nil
								)
								.id(entry.id)
							}
						}
					}

					Section {
						Toggle(
							Strings.Statistics.List.hideZeroStatistics,
							isOn: viewStore.$isHidingZeroStatistics
						)
					} footer: {
						if viewStore.isHidingZeroStatistics {
							Text(Strings.Statistics.List.HideZeroStatistics.help)
						}
					}

					Section {
						Toggle(
							Strings.Statistics.List.statisticsDescription,
							isOn: viewStore.$isHidingStatisticsDescriptions
						)
					} footer: {
						Text(Strings.Statistics.List.StatisticsDescription.help)
					}
				}
				.onChange(of: viewStore.entryToHighlight) {
					guard let id = $0 else { return }
					withAnimation {
						scrollViewProxy.scrollTo(id, anchor: .center)
					}
				}
			}
		})
	}
}

extension Tip {
	static let statisticsDescriptionTip = Tip(
		id: "Statistics.Details.List.Descriptions",
		title: Strings.Statistics.List.StatisticsDescription.Tip.title,
		message: Strings.Statistics.List.StatisticsDescription.Tip.message
	)
}
