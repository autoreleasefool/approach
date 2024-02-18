import AddressLookupServiceInterface
import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

@ViewAction(for: AddressLookup.self)
public struct AddressLookupView: View {
	@Perception.Bindable public var store: StoreOf<AddressLookup>

	public init(store: StoreOf<AddressLookup>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
			List {
				if store.isLoadingAddress {
					Section {
						HStack(alignment: .center) {
							ProgressView()
						}
						.frame(maxWidth: .infinity)
					}
				} else {
					if let error = store.loadingAddressError ?? store.loadingResultsError {
						Section {
							Banner(.titleAndMessage(Strings.Error.Generic.title, error), style: .error)
						}
						.listRowInsets(EdgeInsets())
					}

					Section(Strings.List.results) {
						if store.results.isEmpty {
							Text(Strings.Address.Error.Empty.title)
								.font(.caption)
								.multilineTextAlignment(.center)
						} else {
							ForEach(store.results) { result in
								Button { send(.didTapResult(result.id)) } label: {
									VStack(alignment: .leading, spacing: .tinySpacing) {
										Text(result.completion.wrapped.title)
											.font(.body)
											.foregroundColor(Color(uiColor: .label))
										if !result.completion.wrapped.subtitle.isEmpty {
											Text(result.completion.wrapped.subtitle)
												.font(.caption)
												.foregroundColor(Color(uiColor: .secondaryLabel))
										}
									}
								}
							}
						}
					}
				}
			}
			.searchable(
				text: $store.query,
				prompt: Text(Strings.Action.search)
			)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button(Strings.Action.cancel) { send(.didTapCancelButton) }
				}
			}
			.onFirstAppear { send(.didFirstAppear) }
			.onAppear { send(.onAppear) }
		}
	}
}
