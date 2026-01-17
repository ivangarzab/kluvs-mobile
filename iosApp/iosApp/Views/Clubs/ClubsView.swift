import SwiftUI
import Shared

struct ClubsView: View {
    let userId: String
    @StateObject private var viewModel = ClubDetailsViewModelWrapper()
    @State private var selectedTab = 0
    private let clubId = "0f01ad5e-0665-4f02-8cdd-8d55ecb26ac3" // TODO: Get clubId from user's clubs list via userId

    var body: some View {
        ZStack {
            if viewModel.isLoading {
                LoadingView()
                    .transition(.opacity)
            } else if let error = viewModel.error {
                ErrorView(message: error, onRetry: {
                    viewModel.loadClubData(clubId: clubId)
                })
                .transition(.opacity)
            } else {
                VStack(spacing: 0) {
                    // Tab selector
                    Picker("", selection: $selectedTab) {
                        Text("tab_general").tag(0)
                        Text("tab_active_session").tag(1)
                        Text("tab_members").tag(2)
                    }
                    .pickerStyle(SegmentedPickerStyle())
                    .tint(.brandOrange)
                    .padding(.horizontal)
                    .padding(.top, 8)

                    // Tab content
                    TabView(selection: $selectedTab) {
                        GeneralTab(clubDetails: viewModel.clubDetails)
                            .tag(0)

                        ActiveSessionTab(sessionDetails: viewModel.activeSession)
                            .tag(1)

                        MembersTab(members: viewModel.members)
                            .tag(2)
                    }
                    .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
                }
                .transition(.opacity)
            }
        }
        .animation(.easeInOut(duration: 0.3), value: viewModel.isLoading)
        .animation(.easeInOut(duration: 0.3), value: viewModel.error)
        .onAppear {
            viewModel.loadClubData(clubId: clubId)
        }
    }
}

#Preview {
    ClubsView(userId: "1")
}
